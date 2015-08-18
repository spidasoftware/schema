#!/usr/bin/env node

var http = require('http');
var https = require('https');
var url = require('url');
var prettyjson = require('prettyjson');
var fs = require('fs');
var argv = require('yargs')
    .demand(1)
    .usage('Usage: webhookTool.js <command> [options]')
    .command('register','Register a webhook')
    .command('deregister','Deregister a webhook(s)')
    .command('renew','Renew a webhooks(s)')
    .command('view','View webhooks')
    .command('listen','Listen for callbacks')
    .describe('p','Port to listen on')
    .describe('b','Callback url')
    .describe('a','API url')
    .describe('i','HookId to use')
    .describe('t','Lease time to request')
    .describe('c','Webhook channel')
    .describe('f','Event filter')
    .describe('k','API Token')
    .describe('s','Enable HTTPS')
    .describe('e', 'HTTPS Server Key')
    .describe('r', 'HTTPS Server Cert')
    .describe('u', 'Require Client Side Certificate')
    .describe('v', 'CA Cert to use to verify client side certificates')
    .describe('noUrl','Override default url to null')
    .alias('p','port')
    .alias('b','callback')
    .alias('a','api')
    .alias('i','hookId')
    .alias('t','leaseTime')
    .alias('c','channel')
    .alias('f','filter')
    .alias('h','help')
    .alias('k','token')
    .alias('s','enableHttps')
    .alias('e','httpsKey')
    .alias('r','httpsCert')
    .alias('u','requireClientCert')
    .alias('v','caCert')
    .help('h')
    .default('p',8123)
    .default('b','http://localhost:8123/hook')
    .default('a','http://localhost:8888/projectmanager/webhookAPI')
    .default('t',600)
    .default('c','Project')
    .default('k','admin@spidasoftware.com')
    .argv;

var action = argv._[0];

var reqParams = url.parse(argv.api + '/' + action + '?apiToken=' + argv.token);
reqParams.method = 'POST';
reqParams.headers = {
    'Content-type': 'application/json'
};

var requestJson = {
    channel: argv.channel,
    leaseTime: argv.leaseTime,
};

if (argv.filter) {
    requestJson.eventFilter = argv.filter;
}

if (!argv.noUrl) {
    if (argv.enableHttps && argv.callback.indexOf('https') < 0) {
        requestJson.url = argv.callback.replace('http','https');
    } else {
        requestJson.url = argv.callback;
    }
}

if (argv.hookId) {
    requestJson.hookId = argv.hookId;
}

if (action != 'listen') {
    var proto = reqParams.protocol == 'https:' ? https : http;
    var req = proto.request(reqParams,function(resp) {
        if (resp.statusCode == 200) {
            console.log('Response received');
            var body = '';

            resp.on('data',function(chunk) {
                body += chunk;
            });

            resp.on('end',function() {
                console.log(prettyjson.render(JSON.parse(body)));
            });
        } else {
            console.log('Request failed');
        }
    });

    req.end(JSON.stringify(requestJson));
} else {
    var callbackDisplay = function(req, resp) {
        console.log('=====================================================');
        if (argv.enableHttps) {
            console.log("Client authenticated as: " + req.connection.getPeerCertificate().subject.CN);
        }
        console.log('Callback to ' + req.url);
        var body = '';

        req.on('data',function(chunk) {
            body += chunk;
        });

        req.on('end',function() {
            console.log(prettyjson.render(JSON.parse(body)));
        });

        resp.end();
    };

    if (argv.enableHttps) {
        var opts = {};

        if (argv.httpsKey) {
            opts.key = fs.readFileSync(argv.httpsKey);
        }

        if (argv.httpsCert) {
            opts.cert = fs.readFileSync(argv.httpsCert);
        }

        if (argv.caCert) {
            opts.ca = [fs.readFileSync(argv.caCert)];
        }

        if (argv.requireClientCert) {
            opts.requestCert = true;
            opts.rejectUnauthorized = true;
        }

        https.createServer(opts,callbackDisplay).listen(argv.port);
    } else {
        http.createServer(callbackDisplay).listen(argv.port);
    }
}

