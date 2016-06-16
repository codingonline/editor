openServiceApi= {

		openServiceAPIURL: "http://123.57.32.46:8080/openServiceAPI?",
		
		
		init: function(){
			
		},
		
		getClassesPath: function(){
			return "/data/repo/javaweb/"+ownername+"/"+appname+"/target/classes/";
		},
		
		getProjectPath: function(){
			return "/data/repo/javaweb/"+ownername+"/"+appname+"/";
		},
		
		getCurrentFilePath: function(){
			return Global.ideViewer.codePanel.getActiveTab().path;
		},
		
		initWorkSpace: function(userName, serviceName){
			$.post(this.openServiceAPIURL, {
				action: 'initWorkSpace',
				apptype: apptype,
				appowner: ownername,
				appname: appname,
				serviceowner: userName,
				servicename: serviceName
			}, function(data){
				
			
			});
		},
		
		getServiceAddress: function(serviceName){
			var address='';
			$.ajax({  
		         type : "get",  
		         url : this.openServiceAPIURL,  
		         data : {
						action: 'address',
						servicename: serviceName
					}, 
		         async : false,  
		         success : function(data){  
		        	 var res = $.parseJSON(data);
		        	 address = res.address;
		         }  
		     });
			return address;
		}
		
		
}