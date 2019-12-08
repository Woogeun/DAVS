const bodyParser = require('body-parser');
const express = require('express');
const app = express();

app.use(express.json());

app.post('/', function (req, res) {
	var phase = req.headers["phase"];

	if (phase == "register") {
		console.log(phase);
		// response = invoke(req.body);
		// if (response == "true") { res.send("true"); }
		// else { res.send("fail"); }
	} else {
		console.log(phase);
		// if (response == "true") { res.send("true"); }
		// else if (response == "false") { res.send("false"); }
		// else { res.send("fail"); }
	}
	console.log(req.body);


	res.send('Hello nodejs');
});

app.listen(8888, function () {
  console.log('Example app listening on port 8888!');
});
