var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var multer = require('multer')().array()

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var uploadRouter = require('./routes/upload')
var getfileRouter = require('./routes/getfiles')
var getbaseData = require('./routes/download')

var registerRouter = require('./routes/register')
var loginRouter = require('./routes/login')
var testauthRouter = require('./routes/testauth')
var auth = require('./redis/auth')

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(multer)
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use(function(req, res, next){
	if(req.url != '/register' && req.url != '/login'){
		if(req.body.token == null){
			res.json({err:1, msg:'please login'})
		}else{
			auth(req.body.token, res, next)
		}
	}else{
		next()
	}
})

//在这里添加路由
app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/upload',uploadRouter)
app.use('/files',getfileRouter)
app.use('/download', getbaseData)
app.use('/register', registerRouter)
app.use('/login',loginRouter)
app.use('/testauth', testauthRouter)

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
