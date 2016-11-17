define(function (require, exports, moduel) {
    var $ = require('jquery');
    require('jquery-mobile');

    var defaultOpt = {
        theme: 'android-ics light',
        preset: 'datetime',
        mode: 'mixed',
        dateFormat: 'yyyy-mm-dd',
        timeFormat: 'HH:ii',
        dateOrder: 'yymmdd',
        timeOrder: 'HHii',
        timeWheels: 'HHii',
        setText: '确定',
        cancelText: '取消',
        endYear: 2050,

        dayText: '日',
        monthText: '月',
        yearText: '年',

        hourText: '时',
        minuteText: '分',
        secText: '秒',
        lang: 'zh',
        rows: 3
    };

    var formatMap = {
        '0': 'date',
        '1': 'datetime',
        '2': 'time'
    };
    var datetimeSelect = function (elements, format) {
        format = format || '0';
        var opt = _.clone(defaultOpt);
        opt.preset = formatMap[format];

        require.async('jquery-mobiscroll', function () {
            elements.each(function () {
                $(this).mobiscroll(opt);
            });
        });
    };

    moduel.exports = datetimeSelect;
});
