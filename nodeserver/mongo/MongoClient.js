const mongoose = require('mongoose');
const mongoModel = require('./MongoModel');
const url = 'mongodb://114.55.66.103:27017/f**k996'
const crypto = require('crypto')
const jwt = require('jwt-simple');
const jwtkey = require('../redis/jwtkey')
const redis = require('redis')
const client = redis.createClient(6379, '114.55.66.103');

let userRegister = function (body, callback) {
    mongoose.connect(url)
    let userModel = mongoModel;
    let hash = crypto.createHash('sha256');

    userModel.create({username: body.username, password: hash.update(body.password).digest('hex')}, function (err, res) {
        if(err){
            callback({err:1,msg:err})
        } else {
            callback({err:0,msg:'success!'})
        }
    })
}

let userLogin = function (body, callback) {
    mongoose.connect(url)
    let hash = crypto.createHash('sha256');
    let userModel = mongoModel;
    userModel.find({username: body.username, password: hash.update(body.password).digest('hex')}, function (err, res) {
        if(err){
            console.log(err)
            callback({err:1,msg:'system err'})
        } else if(res != null && res.length != 0){
            console.log(res)
            let token = jwt.encode({
                username: res[0].username,
                time: Date.now()
            }, jwtkey)
            client.set(res[0].username, token);
            client.expire(res[0].username, 1800);
            callback({err:0,msg:token})

        } else {
            callback({err:1,msg:'username or password not matched'})
        }
    })
}

module.exports.userRegister = userRegister
module.exports.userLogin = userLogin
