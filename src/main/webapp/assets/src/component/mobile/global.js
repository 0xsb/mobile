define(function (require, exports, module) {
    var parseQueryString = require('parseQueryString');
    var getParam = function (key) {
        var href = location.href;
        var queryString = href.slice(href.indexOf('?') + 1);
        var params = parseQueryString.parseQueryString(queryString);

        return params[key] || '';
    };

    window.G = {
        userId: getParam('userId') || USER_ID,
        companyId: getParam('companyId') || COMPANY_ID,
        mobile: MOBILEPHONE,
        dbName: getParam('dataBaseName'),
        secretKey: 0,
        getParam: getParam
    };

    window.c_alert = function (text, title, callback) {
        var alterModal = $('<div class="mask alert-mask">\
            <div class="modal-dialog">\
                <div class="dialog-header"></div>\
                <div class="dialog-content"></div>\
                <div class="dialog-footer">\
                    <a href="javascript:" class="dialog-btn">确定</a>\
                </div>\
            </div>\
        </div>').appendTo(document.body);

        if (typeof title === 'function') {
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
});
