var setting = {  
		check: {
	        enable: true,
	        chkStyle: "checkbox",
	        chkboxType: {"Y":"ps", "N":"ps"}
	      },
		data: {
			simpleData: {
				enable: true,
				idKey: "id",
				pIdKey: "pId",
				rootPId: 0
			}
		},
        callback:{
            onCheck:onCheck
        }
    };

    var zTree;  
    var treeNodes;  
      
    $(function(){  
    	$('#communitycodes').val("");
        $.ajax({  
            async : false,  
            cache:false,  
            type: 'POST',  
            dataType : "json",  
            url: "../area/getArea",
            error: function () {//请求失败处理函数  
                alert('请求失败');  
            },  
            success:function(data){ //请求成功后处理函数。    
                //alert(data);  
                treeNodes = data;   //把后台封装好的简单Json格式赋给treeNodes  
            }  
        });
        //初始化ztree树
    	$.fn.zTree.init($("#areaTree"), setting, treeNodes);
    });  
    //选中节点赋值
    function onCheck(e,treeId,treeNode){
        var treeObj=$.fn.zTree.getZTreeObj("areaTree");
        var nodes=treeObj.getCheckedNodes(true);
        var codes="";
        for(var i=0;i<nodes.length;i++){
        if(nodes[i].isParent!=true){
        	codes+=nodes[i].id+",";
            $('#communitycodes').val(codes);
        }
        }
        
    }
    //发送消息提交
    function pushToArea(){
    	var msg=$('#body').val();
    	msg = msg.replace(/\s+/g,""); 
    	var treeObj=$.fn.zTree.getZTreeObj("areaTree");
        var nodes=treeObj.getCheckedNodes(true);
        if(nodes==null||nodes==""){
        	alert("发送对象不能为空，请选择小区！");
        }else if(msg==null||msg==""){
    		alert("请填写系统消息！");
    	}else{
    		if(msg.length>200){
    			alert("请不要超过200个字，当前已输入"+msg.length+"个字");
    		}else{
    			$('#pushForm').submit();
    		}
    	}
        
    }
    
    