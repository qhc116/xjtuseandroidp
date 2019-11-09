const mongoose = require('mongoose');
const model=require('./MongoSchema')
let UserModel = mongoose.model('user', model.UserSchema, 'users')
module.exports = UserModel
