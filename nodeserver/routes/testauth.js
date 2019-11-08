var express = require('express');
var router = express.Router();

/* GET users listing. */
router.post('/', function(req, res, next) {
    res.json("auth access method:post")
});

router.get('/', function(req, res, next) {
    res.json("auth access method:get")
});

module.exports = router;
