var mongoose = require('mongoose')


let PhotoSchema=new mongoose.Schema({filename:String, filedata:String, dateinfo:String, faceuser:String}, {collection:'photo'})

module.exports={PhotoSchema}
