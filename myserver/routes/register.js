var express = require('express');
var router = express.Router();
let multer = require('multer')()
var mongo = require('../mongo/MongoClient')

/* GET users listing. */
router.post('/', function(req, res, next) {
	    mongo.userRegister(req.body, function (info) {
		            res.json(info)
		        })
});



module.exports = router;
