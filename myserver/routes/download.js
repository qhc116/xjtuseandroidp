const express = require('express')
var router = express.Router()
var mongoose = require('mongoose')
//var multer = require('multer')().array()




let Photo = mongoose.model('photo')
router.post('/', function(req,res){
	var id = req.body.id
	Photo.find({'_id':id}, function(err, base64){
		if(err) handleError(err)
		res.json(base64)
	})
})
module.exports = router
