define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');

    var approvalRender = template($('#tmpl-approval').html());

    var run = function () {
        var $content = $('.content');
        var $menu = $('.menu');
        var frameRenders = {};

        var loadFrame = function (frame) {
            if (!frameRenders[frame]) {
                frameRenders[frame] = template($('#tmpl-' + frame).html());
            }
            $content.html(frameRenders[frame]({}));
        };

        $menu.on('click', 'a', function (e) {
            var $el = $(e.target);
            if ($el.is('.active')) {
                return;
            }
            var frame = $el.data('frame');
            loadFrame(frame);
            $menu.find('.active').removeClass('active');
            $el.addClass('active');
        });

        loadFrame($menu.find('a').eq(0).data('frame'));
    };

    module.exports = {
        run: run
    };
});