define(function (require, exports, module) {
    var counter = 0;
    var util = require('./util');
    var loadbox;

    module.exports = {
        show: function (text) {
            if (!loadbox || loadbox.length === 0) {
                loadbox = $('<div class="load-box">\
                        <div class="loader">Loading...</div>\
                        <div class="loader-text"><span></span></div>\
                    </div>')
                    .appendTo(document.body);
                util.disableMove(loadbox);
            }

            counter++;
            counter > 0 && loadbox.show().find('.loader-text > span').text(text || '');
        },
        hide: function () {
            counter--;
            counter <= 0 && loadbox.hide();
        }
    };
});
