var request = require('request');
request.post({
  headers: {'phase' : 'register', 'body': 'asdf'},
  url:     'http://localhost:8888'
}, function(error, response, body){
  console.log(body);
});
