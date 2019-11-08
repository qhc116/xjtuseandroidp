const redis = require('redis')
const jwt = require('jwt-simple');
const jwtkey = require('../redis/jwtkey')

let auth = function (token, res, next) {
    let username
    try{
        username = jwt.decode(token, jwtkey).username
    } catch (e) {
        console.log(e)
        res.json({err:1,msg:'can not decode token'})
    }
    if(username){
        let client = redis.createClient(6379, '114.55.66.103');
        client.get(username, function (err, v) {
            if(err){
                console.log(err)
                res.json({err:1,msg:'redis error!'})
            } else if(v == null) {
                res.json({err:1,msg:'invalide token,please login again!'})
            } else if(v == token){
                client.expire(username,1800)
                next()
            } else {
                res.json({err:1,msg:'token expired,please login again!'})
            }
        })
    }
}
module.exports = auth;