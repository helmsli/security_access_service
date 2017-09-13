angular.module('App', []);
angular.module('App').controller('control_loginwithpass', function() {
	var loginWithPassSession = this;
	loginWithPassSession.countryCode="0086";
	loginWithPassSession.phone="18612131415";
	loginWithPassSession.password="password";	

	loginWithPassSession.nglogin = function()
	{
		console.log(JSON.stringify(loginWithPassSession));
		console.log(loginWithPassSession.countryCode);
		loginwithpass(loginWithPassSession.countryCode,loginWithPassSession.countryCode+loginWithPassSession.phone,loginWithPassSession.password);
	};
	
	
  });

angular.module('App').controller('control_register', function() {
	var registerSession = this;
	registerSession.countryCode="0086";
	registerSession.phone="18612131415";
	registerSession.password="password";	

	registerSession.register = function()
	{
		ajaxRegisterWithAuth(registerSession.countryCode,registerSession.countryCode+registerSession.phone,registerSession.password,registerSession.transid,registerSession.authcode);
	};
	registerSession.getAuthCode=function()
	{
		ajaxGetAuthCode(registerSession.countryCode,registerSession.countryCode+registerSession.phone,function success(result,authCode)
		{
			if(result==0)
			{
				registerSession.transid=authCode.transid;
			}
		},
		function error(xhr,testStatus){
			
		});		
	};
	
	
  });

/*
angular.module('app').controller('SecondCtrl',['$scope',function($scope){
    
}]);
*/