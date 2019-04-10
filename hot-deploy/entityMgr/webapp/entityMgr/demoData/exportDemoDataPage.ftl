<h2>数据引接</h2>
<div class="gap"></div>
<div id="main"></div>
<style>
    .choose-img {
        margin-left: 5px;
        margin-top: auto;
        cursor: pointer;
        background-image: url(../../entityMgr/images/button/add-black-simple.png);
        display: inline-block;
        width: 16px;
        height: 16px;
    }

    .entity-list-main .active {
        background-color: #bbddff;
    }
</style>
    <div class="add-dialog-content">
        <form class="add-form" action="exportDemoData">
            <div class="item">
                <label class="label" style="text-align: left">
                    1.输入要导出的表表名
                    <br>2.每行填一个表名
                    <br>3.主表在上,从表在下
                </label>

                <div class="value">
                    <textarea name="entityNameArray" data-validation="required" rows="20"></textarea>
                </div>
            </div>
            <div>
                <button class="button-weak" type="submit">导出</button>
            </div>
        </form>

    </div>



<script>


</script>