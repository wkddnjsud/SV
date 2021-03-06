<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <title>SV Company</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimal-ui">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="description" content="CodedThemes">
    <meta name="keywords" content="flat ui, admin  Admin , Responsive, Landing, Bootstrap, App, Template, Mobile, iOS, Android, apple, creative app">
    <meta name="author" content="CodedThemes">
    
    <jsp:include page="/WEB-INF/views/common/common.jsp"/>
    
</head>

<body>
    <!-- Pre-loader start -->
    <div class="theme-loader">
        <div class="ball-scale">
            <div class='contain'>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
            </div>
        </div>
    </div>
    <!-- Pre-loader end -->
    <div id="pcoded" class="pcoded">
        <div class="pcoded-overlay-box"></div>
        <div class="pcoded-container navbar-wrapper">
			
			<jsp:include page="/WEB-INF/views/common/sideMenu.jsp"/>
		
			<jsp:include page="/WEB-INF/views/common/topMenu.jsp"/>
			
            <div class="pcoded-main-container">
                <div class="pcoded-wrapper">
                    <div class="pcoded-content">
                        <div class="pcoded-inner-content">

                            <div class="main-body">
                                <div class="page-wrapper">
                                    <!-- Page-header start -->
                                    <div class="page-header card">
                                        <div class="row align-items-end">
                                            <div class="col-lg-8">
                                                <div class="page-header-title">
                                                    <i class="icofont icofont icofont icofont-file-document bg-c-pink"></i>
                                                    <div class="d-inline">
                                                        <h4>????????????</h4>
                                                        <span>Share the Vision??? ????????? Zoom??? ?????? ???????????? ????????? ??? ????????????.</span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-4" >
                                            	<div class="page-header-breadcrumb">
                                                    <ul class="breadcrumb-title" >
                                                        <li class="breadcrumb-item">
                                                        	<input type="button" class="btn btn-danger btn-round btn-outline-danger" id="meetingBtn" value="?????? ????????????"/>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Page-header end -->

                                    <div class="page-body">
                                        <div class="row">
                                            <div class="col-sm-12">
                                                <div class="card">
                                                    <div class="card-header">
                                                        <h5>Share the Vision??? ????????? Zoom??????</h5>
                                                        <br><br>
                                                        <span><i class="icofont icofont-exclamation icofont-2x"></i><font size="5">????????????</font></span>
                                                        <span><i class="icofont icofont-check"></i>?????? ???????????? ????????? ?????? ????????? ????????? ????????? ???????????????.</span>
                                                        <span><i class="icofont icofont-check"></i>????????? ???????????? ?????? URL??? ???????????????.</span>
                                                        <span><i class="icofont icofont-check"></i>????????? ?????? ??? ???????????? ???????????? ????????? ????????? ????????? ??? ????????????.</span>
                                                        <span><i class="icofont icofont-check"></i>????????? ????????? ??? ???????????? ????????? ?????? ????????? ????????? ??????????????? ????????????.</span>
                                                        <span><i class="icofont icofont-check"></i>?????? ?????? ????????? ???????????? ?????? ????????? ???????????? ????????? ????????? ????????? ????????? ????????????.</span>
                                                        <span><i class="icofont icofont-check"></i>?????? URL??? ????????? ????????? ???????????? URL??? ???????????????.</span>
                                                        
                                                    </div>
                                                    <div class="card-block" id="contentDiv" style="display:none;">
                                                    	<div class="form-group">
														    <i class="ti-layers">&nbsp;</i><label for="urlTextarea">?????? URL : </label>
														    <textarea onclick="copyText();" style="resize:none;" class="form-control" id="urlTextarea" rows="3"></textarea>
														 </div>
														 <br>
														 <input type="hidden" id="startUrl"/>
														 <div style="text-align: center;">
		                                                     <button onclick="openMeeting()" class="btn btn-danger" style="margin-right:15px;">?????? ????????????</button>
		                                                     <button onclick="location.href='home.me'" class="btn btn-inverse btn-outline-inverse">??????</button>
														 </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div id="styleSelector">

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
<script>
	count = 1;
	
	$("#meetingBtn").on("click",function(){
		if(count % 2 == 1){
			makeMeeting();
			$(this).val("?????? ??????");
		} else {
			console.log("????????? ?????????");
			$("#contentDiv").hide();
			$(this).val("?????? ????????????");
		}
		count++;
	});

	function makeMeeting(){
		var area = document.getElementById("urlTextarea");
		var start = document.getElementById("startUrl");
		
		$.ajax({
			url: "meeting.pr",
			dataType: "json",
			success: function(data){
				if(data != null){
					area.value = data.join_url;
					start.value = data.start_url;
					$("#contentDiv").show();
				}
				
			},
			error: function(data){
				console.log('?????????');
				
			}
		})
	}
	
	function copyText(){
		var area = document.getElementById("urlTextarea");
		
		area.select();
		document.execCommand("copy");
		
		alert("?????? URL??? ?????????????????????.");
	}
	
	function openMeeting(){
		var url = document.getElementById("startUrl").value;
		window.open(url);
	}
	
</script>

</html>
