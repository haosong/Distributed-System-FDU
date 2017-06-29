<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>build</title>
    <script src="js/jquery-3.2.1.js"></script>
</head>
<body>
<button id="btn">点我</button>
<div id = "province">
</div>

<div id="grade">

</div>

<script type="text/javascript">
    $("#btn").click(getKNN());
    function getKNN() {
        var query = {"lng":-73.97000655, "lat": 40.76098703, "k": 5};
        $.ajax({
            type : "post",
            async : false, //同步执行
            url : "/KNNQuery",
            data: JSON.stringify(query),
            dataType : "json", //返回数据形式为json
            success : function(result) {
                console.log("success");
                if (result) {
                    console.log(result);
                }
            },
            error : function(result) {
                console.log("error");
            }
        });
    }

    function getWithin() {
        var query = [{"lng":-73.980844, "lat": 40.758703},
            {"lng":-73.987214, "lat": 40.761369},
            {"lng":-73.990839, "lat": 40.756400},
            {"lng":-73.984422, "lat": 40.753642},
            {"lng":-73.980844, "lat": 40.758703}];

        $.ajax({
            type : "post",
            async : false, //同步执行
            url : "/WithinQuery",
            data: JSON.stringify(query),
            dataType : "json", //返回数据形式为json
            success : function(result) {
                console.log("success");
                if (result) {
                    console.log(result);
                }
            },
            error : function(result) {
                console.log("error");
            }
        });
    }

</script>
</body>
</html>