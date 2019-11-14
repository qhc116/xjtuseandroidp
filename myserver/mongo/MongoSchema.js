let UserSchema={username:  {type: String, unique: true,require:true},
	                password:{type:String,required: true},
	                hasface:{type:Boolean,default:false}}

module.exports={UserSchema}
