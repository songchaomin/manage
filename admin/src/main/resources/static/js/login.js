if(window.top!==window.self){
    window.top.location=window.location
};
layui.use(['element'], function () {
    var $ = layui.jquery;
    var location = window.location.href;
    if (location){
        var startStr = location.indexOf("?");
        if (startStr!=-1){
            var username = location.substring(startStr+1,startStr+1+location.length);
            $("#username").val(username);
        }
    }
    
    
    $(document).on('click', '.captcha-img', function () {
        var src = this.src.split("?")[0];
        this.src = src + "?" + Math.random();
    });
    $(document).on('click', '.ajax-login', function (e) {
        e.preventDefault();
        var form = $(this).parents("form");
        var url = form.attr("action");
        var serializeArray = form.serializeArray();
        $.post(url, serializeArray, function (result) {
            if(result.code !== 200){
                $('.captcha-img').click();
            }
            $.fn.Messager(result);
        });
    });
    $('.layui-layer-loading').hide();
});