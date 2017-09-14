angular.module('App', []);
angular.module('App').controller('control_loginwithpass', function() {
	var loginWithPassSession = this;
	loginWithPassSession.countryCode="0086";
	loginWithPassSession.phone="18612131415";
	loginWithPassSession.password="password";	

	loginWithPassSession.nglogin = function()
	{
		loginWithPassSession.getPublicKey();
	};
	
	loginWithPassSession.getPublicKey=function()
	{
		ajaxGetRsaPublicKey(loginWithPassSession.countryCode,loginWithPassSession.countryCode+loginWithPassSession.phone,function success(result,authCode)
		{
			if(result==0)
			{
				loginWithPassSession.transid=authCode.transid;
				loginWithPassSession.publicKey=authCode.publicKey;
				loginWithPassSession.random = authCode.random;
				loginWithPassSession.crcType = authCode.crcType;
                
				var encrypt = new JSEncrypt();
				console.log("**************");
				console.log(loginWithPassSession.random);
				console.log(loginWithPassSession.password);
				
				 encrypt.setPublicKey(loginWithPassSession.publicKey);
				 var encrypted = encrypt.encrypt(loginWithPassSession.random+loginWithPassSession.password);
			
				console.log(JSON.stringify(loginWithPassSession));
				console.log(")))))))))))))))))))))");
				console.log(encrypted);
				console.log("((((((((((((((((((((((");
				ajaxLoginWithPass(loginWithPassSession.transid,loginWithPassSession.countryCode,loginWithPassSession.countryCode+loginWithPassSession.phone,encrypted);

			}
		},
		function error(xhr,testStatus){
			
		});		
	};

	
	
  });

angular.module('App').controller('control_loginwithcode', function() {
	var loginWithPassSession = this;
	loginWithPassSession.countryCode="0086";
	loginWithPassSession.phone="18612131415";
	loginWithPassSession.password="password";	

	loginWithPassSession.nglogin = function()
	{
		loginWithPassSession.getPublicKey();
	};
	
	loginWithPassSession.getPublicKey=function()
	{
		ajaxGetRsaPublicKey(loginWithPassSession.countryCode,loginWithPassSession.countryCode+loginWithPassSession.phone,function success(result,authCode)
		{
			if(result==0)
			{
				loginWithPassSession.transid=authCode.transid;
				loginWithPassSession.publicKey=authCode.publicKey;
				loginWithPassSession.random = authCode.random;
				loginWithPassSession.crcType = authCode.crcType;
                
				var encrypt = new JSEncrypt();
				console.log("**************");
				console.log(loginWithPassSession.random);
				console.log(loginWithPassSession.password);
				
				 encrypt.setPublicKey(loginWithPassSession.publicKey);
				 var encrypted = encrypt.encrypt(loginWithPassSession.random+loginWithPassSession.password);
			
				console.log(JSON.stringify(loginWithPassSession));
				console.log(")))))))))))))))))))))");
				console.log(encrypted);
				console.log("((((((((((((((((((((((");
				ajaxLoginWithPass(loginWithPassSession.transid,loginWithPassSession.countryCode,loginWithPassSession.countryCode+loginWithPassSession.phone,encrypted);

			}
		},
		function error(xhr,testStatus){
			
		});		
	};

	
	
  });

angular.module('App').controller('control_register', function() {
	var registerSession = this;
	registerSession.countryCode="0086";
	registerSession.phone="18612131415";
	registerSession.password="password";	

	registerSession.register = function()
	{
		 var encrypt = new JSEncrypt();
		 encrypt.setPublicKey(registerSession.publicKey);
		 var encrypted = encrypt.encrypt(registerSession.random+registerSession.password);
		ajaxRegisterWithAuth(registerSession.countryCode,registerSession.countryCode+registerSession.phone,encrypted,registerSession.transid,registerSession.authcode);
	};
	registerSession.getAuthCode=function()
	{
		ajaxGetAuthCode(registerSession.countryCode,registerSession.countryCode+registerSession.phone,function success(result,authCode)
		{
			if(result==0)
			{
				registerSession.transid=authCode.transid;
				registerSession.publicKey=authCode.publicKey;
				registerSession.random = authCode.random;
				registerSession.crcType = authCode.crcType;

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