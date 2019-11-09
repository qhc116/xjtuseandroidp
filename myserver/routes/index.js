var express = require('express');
var router = express.Router();
var fs = require('fs')
var mongoose = require('mongoose')


/* GET home page. */
router.get('/test', function(req, res, next) {
	res.json()
});

module.exports = router;
