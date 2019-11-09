var express = require('express')
var router = express.Router()


//var multer = require('multer')().array()


const mongoose = require('mongoose')

let Photo=mongoose.model('photo')
router.post('/', function(req,res,next){
	console.log(req.body.filename)
	//接受post请求时写入一条测试数据	
	Photo.create({filename:req.body.filename, filedata:req.body.filedata, dateinfo:req.body.dateinfo, faceuser:req.body.faceuser}, function(err, data){
		if(err) return handleError(err)
		res.json(data)
	})
})

module.exports = router
