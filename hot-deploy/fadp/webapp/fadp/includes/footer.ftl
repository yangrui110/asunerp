
</div end-for-center>


<div class="ui-layout-south">
<div style="text-align: center">
    ©版权所有
</div>


</div>



<script>
    $(function(){
        var nsOpt={
            resizable:false
        };
        $('body').layout({
            applyDemoStyles: false
            ,west__size:250
            ,spacing_open:3
            ,north:$.extend({size:50},nsOpt)
            ,south:$.extend({size:20},nsOpt)
        });

    });
</script>

</body>

</html>
