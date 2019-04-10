<div class="pager">
    <div>
        <select onchange="pageGo(1)" name="pageSize"  style="width:70px; height:30px; line-height:26px;float:left;">
            <#list pageSizeList as pageSize>
                <option <#if pageSize==parameters.pageSize>selected="selected" </#if> value="${pageSize}">${pageSize}</option>
            </#list>
        </select>

        <span style="float:left; line-height:32px;">当前页码 ${currPage}  / ${totalPages} , 共 ${totalRow} 条记录 </span>
    </div>
    <div style="float:right; margin-right:20px;">
        <input type="hidden" id="currPage" name="currPage" value="${currPage}">

        <td colspan="3"><button id="button1" class="button-blue" type="button" >首页</button></td>
        <td colspan="3"><button id="button2" class="button-blue"  type="button" >上一页</button></td>
        <td colspan="3"><button id="button3"  class="button-blue" type="button" >下一页</button></td>
        <td colspan="3"><button id="button4"  class="button-blue" type="button" >末页</button></td>
        <script>
            var currentPage=${currPage};
            var totalPages=${totalPages};
            function pageGo(page){
                $('#currPage').val(page);
                $('#dataModelForm').submit();
            }
            $('#button1').click(function () {
                if (currentPage == 1||currentPage == 0) {
                    return;
                }
                pageGo(1);

            });
            $('#button2').click(function () {
                if (currentPage == 1 ||currentPage == 0) {
                    return;
                }
                pageGo(currentPage - 1);

            });
            $('#button3').click(function () {
                if (currentPage == totalPages) {
                    return;
                }
                pageGo(currentPage + 1);

            });
            $('#button4').click(function () {
                if (currentPage == totalPages) {
                    return;
                }
                pageGo(totalPages);

            });
        </script>
    </div>
</div>
</form>