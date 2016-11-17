define(function (require, exports, module) {
    var $ = require('jquery');
    var loader = require('./loader');
    var util = require('./util');

    var global_viewer;
    var ImgViewer = function () {
        this.init();
    };
    ImgViewer.prototype.init = function () {
        var _this = this;
        this.$el = $('<div class="mask"><div class="viewimage">\
                <img src="" alt="">\
                <a rel="external" class="downloadLink" href="">下载</a>\
            </div></div>').appendTo(document.body).hide();
        this.$dlink = this.$el.find('.downloadLink');
        this.$img = this.$el.find('img');
        this.$viewer = this.$el.find('.viewimage');
        this.$viewer.css('lineHeight', this.$el.height() + 'px');

        this.elWidth = this.$el.width();
        this.elHeight = this.$el.height();
        this.minScale = 75;
        this.maxScale = 500;

        this.$img.on('load', function (e) {
            loader.hide();
            _this.$el.show();

            var naturalWidth = _this.$img.prop('naturalWidth'),
                naturalHeight = _this.$img.prop('naturalHeight');
            if (naturalWidth && naturalHeight) {
                var w = (naturalWidth / _this.elWidth) * 100,
                    h = (naturalHeight / _this.elHeight) * 100;
                this.maxScale = Math.max(w, h);
            }
            else {
                this.maxScale = 500;
            }
        });

        this.$el.on('click', function (e) {
            if (!$(e.target).is('.downloadLink')) {
                _this.$el.hide();
            }
        });
        util.disableMove(this.$el);
        this.initTouch();
    };
    ImgViewer.prototype.initTouch = function () {
        var _this = this;
        var $img = this.$img,
            $viewer = this.$viewer;
        var startScale = 100,
            startScroll = { left: 0 ,top: 0 };

        util.touchMove(this.$el, {
            start: function (startPoints, isMulti) {
                if (isMulti) startScale = parseFloat($img.css('maxWidth').replace('%'));
                else startScroll = { left: $viewer[0].scrollLeft ,top: $viewer[0].scrollTop };
            },
            move: function (info, isMulti) {
                if (isMulti) {
                    var scaleChange = (info.currentLong / info.startLong);
                    var newScale = startScale * scaleChange;

                    if (newScale < _this.minScale) newScale = _this.minScale;
                    else if (newScale > _this.maxScale) newScale = _this.maxScale;

                    $img.css({'maxWidth': newScale + '%', 'maxHeight': newScale + '%'});
                }
                else {
                    $viewer[0].scrollLeft = startScroll.left + info.startPoints.x1 - info.currentPoints.x1;
                    $viewer[0].scrollTop = startScroll.top + info.startPoints.y1 - info.currentPoints.y1;
                }
            }
        });
    };
    ImgViewer.prototype.show = function (url, showDlink) {
        showDlink ? this.$dlink.attr('href', url).show() : this.$dlink.hide();

        this.$img.css({'maxWidth': '90%', 'maxHeight': '90%'});
        if (this.$img.attr('src') !== url) {
            loader.show('图片读取中，请稍候');
            this.$img.attr('src', url);
        }
        else {
            loader.hide();
            this.$el.show();
        }
    };

    module.exports = {
        show: function (url, showDlink) {
            if (!global_viewer) global_viewer = new ImgViewer();
            global_viewer.show(url, showDlink);
        }
    };
});
