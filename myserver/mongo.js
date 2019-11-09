const  mongoose=require('mongoose')
const model=require('./models')


let Photo=mongoose.model("photo",model.PhotoSchema)
