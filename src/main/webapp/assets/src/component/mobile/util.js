define(function (require, exports, module) {
    var parseQueryString = require('parseQueryString');

    var getParam = function (key) {
        var href = location.href;
        var queryString = href.slice(href.indexOf('?') + 1);
        var params = parseQueryString.parseQueryString(queryString);

        return params[key] || '';
    };

    var customAlert = function (text, title, callback) {
        var alterModal = $('<div class="mask dialog-mask">\
            <div class="modal-dialog">\
                <div class="dialog-header"></div>\
                <div class="dialog-content"></div>\
                <div class="dialog-footer">\
                    <a href="javascript:" class="dialog-btn">确定</a>\
                </div>\
            </div>\
        </div>').appendTo(document.body);

        if (typeof title !== 'string') {
            callback = title;
            title = '提示';
        }
        text = text.toString().split('\r\n');
        var content = _.map(text, function (t) {
            return $('<p></p>').text(t);
        });

        alterModal.find('.dialog-header').text(title);
        alterModal.find('.dialog-content').append(content);
        alterModal.show();

        var d = alterModal.find('.modal-dialog');
        var w = d.width(), h = d.height();
        d.css({
            'marginLeft': (w / -2) + 'px',
            'marginTop': (h / -2) + 'px'
        });

        alterModal.on('click', '.dialog-btn', function () {
            alterModal.remove();
            callback && callback();
        });
    };
    var customConfirm = function (text, title, callback) {
        var alterModal = $('<div class="mask dialog-mask">\
            <div class="modal-dialog">\
                <div class="dialog-header"></div>\
                <div class="dialog-content"></div>\
                <div class="dialog-footer">\
                    <a href="javascript:" class="dialog-btn" data-action="ok">确定</a>\
                    <a href="javascript:" class="dialog-btn" data-action="cancel">取消</a>\
                </div>\
            </div>\
        </div>').appendTo(document.body);

        if (typeof title !== 'string') {
            callback = title;
            title = '提示';
        }
        text = text.toString().split('\r\n');
        var content = _.map(text, function (t) {
            return $('<p></p>').text(t);
        });

        alterModal.find('.dialog-header').text(title);
        alterModal.find('.dialog-content').append(content);
        alterModal.show();

        var d = alterModal.find('.modal-dialog');
        var w = d.width(), h = d.height();
        d.css({
            'marginLeft': (w / -2) + 'px',
            'marginTop': (h / -2) + 'px'
        });

        alterModal.on('click', '.dialog-btn', function (e) {
            alterModal.remove();

            var isOk = $(e.target).data('action') === 'ok';
            callback && callback(isOk);
        });
    };
    var transColorCode = function (str) {
        var code = 0, temp = 0, codeStr = '', i = 0;
        for (i = 0; i < str.length; i++) {
            code += str.charCodeAt(i);
        }
        for (i = 0; i < 3; i++) {
            temp = code % 192;
            codeStr += ('00' + temp.toString(16)).slice(-2);
            code = Math.round(code * temp / 127);
        }
        return codeStr;
    };
    var latterString = function (str, length) {
        if (!length || length <= 0) {
            length = 2;
        }
        return name.slice(-1 * length);
    };
    var disableMove = function (elem) {
        elem.on('touchmove', function (e) {
            e.preventDefault();
            e.stopPropagation();
        });

        var _show = elem.show,
            _hide = elem.hide;
        elem.show = function () {
            $('body').css('overflow', 'hidden');
            return _show.apply(elem);
        };
        elem.hide = function () {
            $('body').css('overflow', '');
            return _hide.apply(elem);
        };
    };

    var trianEdgeLong = function (x1, y1, x2, y2) {
        var long = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return Math.abs(long);
    };
    var touchMove = function (elem, cbs) {
        var isTouching = false,
            startPoints = { x1: 0, y1: 0, x2: 0, y2: 0 };
        elem.on('touchstart', function (e) {
            var ev = e.originalEvent;
            isTouching = true;
            var isMulti = ev.touches.length > 1;

            startPoints.x1 = ev.touches[0].clientX;
            startPoints.y1 = ev.touches[0].clientY;
            if (isMulti) {
                startPoints.x2 = ev.touches[1].clientX;
                startPoints.y2 = ev.touches[1].clientY;
            }

            cbs.start && cbs.start(startPoints, isMulti);
        });
        elem.on('touchmove', function (e) {
            if (!isTouching) { return; }
            var ev = e.originalEvent;
            var isMulti = ev.touches.length > 1;

            var currentPoints = {
                x1: ev.touches[0].clientX,
                y1: ev.touches[0].clientY,
                x2: isMulti ? ev.touches[1].clientX : 0,
                y2: isMulti ? ev.touches[1].clientY : 0
            };
            var startLong = 0, currentLong = 0;
            if (isMulti) {
                startLong = trianEdgeLong(startPoints.x1, startPoints.y1, startPoints.x2, startPoints.y2);
                currentLong = trianEdgeLong(currentPoints.x1, currentPoints.y1, currentPoints.x2, currentPoints.y2);
            }

            cbs.move && cbs.move({
                startLong: startLong,
                currentLong: currentLong,
                startPoints: startPoints,
                currentPoints: currentPoints
            }, isMulti);
        });
        elem.on('touchend', function (e) {
            isTouching = false;
            var ev = e.originalEvent;
            var hasTouches = ev.touches.length > 0;
            var isMulti = ev.touches.length > 1;

            var endPoints = {
                x1: hasTouches ? ev.touches[0].clientX : 0,
                y1: hasTouches ? ev.touches[0].clientY : 0,
                x2: isMulti ? ev.touches[1].clientX : 0,
                y2: isMulti ? ev.touches[1].clientY : 0
            };
            cbs.end && cbs.end(startPoints, endPoints, isMulti);
        });
        elem.on('touchcancel', function () {
            isTouching = false;
            cbs.cancel && cbs.cancel();
        });
    };

    var formatDate = function (paramDate, format) {
        var o = {
            'M+': paramDate.getMonth() + 1, //month
            'd+': paramDate.getDate(), //day
            'h+': paramDate.getHours() % 12, //hour
            'H+': paramDate.getHours(),
            'm+': paramDate.getMinutes(), //minute
            's+': paramDate.getSeconds(), //second
            'q+': Math.floor((paramDate.getMonth() + 3) / 3), //quarter
            'S': paramDate.getMilliseconds() //millisecond
        };
        if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (paramDate.getFullYear() + '').substr(4 - RegExp.$1.length));
        for (var k in o) {
            if (new RegExp('(' + k + ')').test(format)) {
                format = format.replace(RegExp.$1,
                    RegExp.$1.length == 1 ? o[k] :
                    ('00' + o[k]).substr(('' + o[k]).length));
            }
        }

        return format;
    };

    var cacheMap = {};
    var util = {
        constant: {
            get userId () {
                if (!cacheMap['_userId']) { cacheMap['_userId'] = getParam('userId') || USER_ID; }
                return cacheMap['_userId'];
            },
            get companyId () {
                if (!cacheMap['_companyId']) { cacheMap['_companyId'] = getParam('companyId') || COMPANY_ID; }
                return cacheMap['_companyId'];
            },
            get mobile () {
                if (!cacheMap['_mobile']) { cacheMap['_mobile'] = getParam('mobile') || MOBILEPHONE; }
                return cacheMap['_mobile'];
            },
            get dbName () {
                if (!cacheMap['_dbName']) { cacheMap['_dbName'] = getParam('dataBaseName') || MOBILEPHONE; }
                return cacheMap['_dbName'];
            },
            get secretKey () {
                return 0;
            },
            get userName () {
                if (!cacheMap['_userName']) { cacheMap['_userName'] = getParam('userName') || USER_NAME; }
                return cacheMap['_userName'];
            },
            get deptName () {
                if (!cacheMap['_deptName']) { cacheMap['_deptName'] = getParam('deptName') || DEPT_NAME; }
                return cacheMap['_deptName'];
            }
        },
        getParam: getParam,
        alert: customAlert,
        confirm: customConfirm,
        disableMove: disableMove,
        touchMove: touchMove,
        transColorCode: transColorCode,
        formatDate: formatDate
    };

    window.G = {
        userId: getParam('userId') || USER_ID,
        companyId: getParam('companyId') || COMPANY_ID,
        mobile: MOBILEPHONE,
        dbName: getParam('dataBaseName'),
        secretKey: 0,
        getParam: getParam
    };

    module.exports = util;
});
