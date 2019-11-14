const express = require('express')
var mongoose = require('mongoose')
var router = express.Router()


let Photo=mongoose.model('photo')
router.post('/', function(req,res){
	Photo.find({filename:/.*/},{filename:1},function(err, filedata){
		if(err) return handleError(err)
		console.log(filedata)
		res.json(filedata)
	})
})

module.exports = router
