'use strict';

const { FileSystemWallet, Gateway } = require('fabric-network');
const path = require('path');
const bodyParser = require('body-parser');
const express = require('express');
var sha256 = require('js-sha256');

const ccpPath = path.resolve(__dirname, '..', '..', 'first-network', 'connection-org1.json');
const app = express();

function toHash(data){
    return sha256(data);
}

async function verification(data) {
    try {

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = new FileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);

        // Check to see if we've already enrolled the user.
        const userExists = await wallet.exists('user1');
        if (!userExists) {
            console.log('An identity for the user "user1" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }

        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccpPath, { wallet, identity: 'user1', discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('fabcar');

        const result = await contract.evaluateTransaction('queryAllDocuments');

        var flag = false;
        const find = JSON.parse(result.toString());
        for (let i = 0; i < find.length; i++) {
            if (find[i]["Record"]["model"] == data) {
                flag = true;
                break;
            }
        }

        console.log(`result is: ${flag.toString()}`);

        return true;

    } catch (error) {
        console.error(`Failed to evaluate transaction: ${error}`);
        process.exit(1);
    }
}


async function registration(data, number) {
    try {

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = new FileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);

        // Check to see if we've already enrolled the user.
        const userExists = await wallet.exists('user1');
        if (!userExists) {
            console.log('An identity for the user "user1" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }

        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccpPath, { wallet, identity: 'user1', discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('fabcar');

        // Submit the specified transaction.
        await contract.submitTransaction('createDocument', number, 'test', toHash(data), 'Black', 'Tom');
        console.log('Transaction has been submitted');

        // Disconnect from the gateway.
        await gateway.disconnect();

        return true;

    } catch (error) {
        console.error(`Failed to submit transaction: ${error}`);
        process.exit(1);
    }
}

var number = 0;
app.use(express.json());

app.post('/', function (req, res) {
    number = number + 1;
    var phase = req.headers["phase"];
    var data = req.headers["body"];

	if (phase == "register") { 
        console.log(phase);
        console.log(data);
        console.log(number);

        var response = registration(toHash(data), "DOC".concat(number.toString()));
        if (response) { res.send("true"); }
        else { res.send("fail"); }

	} else {
		console.log(phase);
        var response = verification(toHash(data));
        if (response) { res.send("true"); }
        else { res.send("fail"); }
	}

});

app.listen(8888, function () {
  console.log('Example app listening on port 8888!');
});
