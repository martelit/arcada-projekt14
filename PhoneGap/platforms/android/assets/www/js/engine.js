	//Define engine.js-global variables
				
		var darknessCanvas;
		var darknessContext;
		var darkness = false;
		var nightmare = false;
		var tokenTimer = 0;
		var tokenLightOn;
		var tokenLightX = 0;
		var tokenLightY = 0;
		var tokenLightTime = 150;
		var tokenFinder = true;
		
		var symbolsCanvas;
		var symbolsContext;
		
		//Maze definitions
		var canvas;
		var context;
		var lsSize = localStorage.size;
		var lsMode = localStorage.mode;
		var lsColor = localStorage.color;
		var mazeName = "null";
		var skinName = "null";
		
		//Ball movement definitions
		var speedX = 0;
		var speedY = 0;
		var speedUnit = 0.2;
		var maxSpeed = 3;
		var bounceSensitivity = speedUnit + 0.01;
		var bounceSpeedDimish = -0.4;
		
		//Ball definitions
		var ball = null;
		var ballPosition = null;
		var ballLeft = 0;
		var ballRight = 0;
		var ballTop = 0;
		var ballBottom = 0;
		var ballRadius = 0;
		var ballSize = 0;
		
		//Acceleration definitions
		var accX = 0;
		var accY = 0;
		
		//Window size definitions
		var windowWidth = $(window).width();
		var windowHeight = $(window).height();		

	
    // The watch id references the current `watchAcceleration`
    var watchID = null;

    // Wait for Cordova to load
    //
    document.addEventListener("deviceready", onDeviceReady, false);
	
    // Cordova is ready
    //	
    function onDeviceReady() {
		
		//Load from settings
		pickMaze();		
		pickMode();
		
		//Set up the canvas
		canvas = document.getElementById("canvas");
		context = canvas.getContext("2d");
				
		//Graphical skin layer setup
		skinSetup();
		
		//BALL SETUP, ballSize as parameter
		ballSetup(12);

		//Symbol layer setup
		symbolsSetup();
		
		//Draw the maze background
		window.setTimeout(function(){drawMaze("maps/"+mazeName+".png");}, 2000);
		if(nightmare){
			window.setTimeout(function(){setDarkness();}, 3000);
		}
		startWatch();
		
    }

	//Drawing the maze
	function drawMaze(mazeFile){
		var imgMaze = new Image();
		imgMaze.onload = function() {
			//Resize the canvas to match the mace picture
			canvas.width = windowWidth;
			canvas.height = windowHeight;
			
			//Draw the maze
			context.drawImage(imgMaze, ((windowWidth/2)-(windowHeight/2)),0, windowHeight, windowHeight);		
		}
		imgMaze.src = mazeFile;
	}
    // Start watching the acceleration
    //
    function startWatch() {

        var options = { frequency: 30 };

        watchID = navigator.accelerometer.watchAcceleration(onSuccess, onError, options);
    }

    // Stop watching the acceleration
    //
    function stopWatch() {
        if (watchID) {
            navigator.accelerometer.clearWatch(watchID);
            watchID = null;
        }
    }

    // onSuccess: Get a snapshot of the current acceleration
    // BALL MOVEMENT
	
    function onSuccess(acceleration) {    
							
		//Fetch ACCELERATION values, X=Y && Y=X due to forced landscape
		accX = acceleration.y;
		accY = acceleration.x;
		
		//MOVE TO RIGHT, accX is POSITIVE
		if(accX > 1){
			if(speedX <= maxSpeed){
				if(speedX <= 0){
					speedX += speedUnit*2;
				}
				else{
					speedX += speedUnit;
				}
			}			
		}
		//MOVE TO LEFT, accX is NEGATIVE
		else if(accX < -1){
			if(speedX >= -maxSpeed){
				if(speedX >= 0){
					speedX += -speedUnit*2;
				}
				else{
					speedX += -speedUnit;
				}
			}
		}
		//SLOW DOWN, accX is NEUTRAL
		else{
			if(speedX > 0){
				speedX += -speedUnit/2;
			}
			else if(speedX < 0){
				speedX += speedUnit/2;
			}
		}
		//MOVE DOWNWARDS, accY is POSITIVE
		if(accY > 1){
			if(speedY <= maxSpeed){
				if(speedY <= 0){
					speedY += speedUnit*2;
				}
				else{
					speedY += speedUnit;
				}
			}
		}
		//MOVE UPWARDS, accY is NEGATIVE
		else if(accY < -1){
			if(speedY >= -maxSpeed){
				if(speedY >= 0){
					speedY += -speedUnit*2;
				}
				else{
					speedY += -speedUnit;
				}
			}
		}
		//SLOW DOWN, accY is NEUTRAL
		else{
			if(speedY > 0){
				speedY += -speedUnit/2;
			}
			else if(speedY < 0){
				speedY += speedUnit/2;
			}
		}
			
		//ALL MODES, check for collision
		checkCollision();	

		//VISIBLE MODE, animate ball movement
		if(!darkness){
			ball.velocity({top:"+="+speedY, left:"+="+speedX}, {duration: 0.1});
		}
		
		//DARKMODES, clear old ball
		if(darkness){
			clearBall(ballLeft,ballTop);
		}
		
		//ALL MODES, update position variables
		ballLeft += speedX;
		ballRight = ballLeft + ballSize;
		ballTop += speedY;
		ballBottom = ballTop + ballSize;
		
		//DARKMODES, draw new ball, light and tokenlight
		if(darkness){
			drawCircle(ballLeft, ballTop);
			if(nightmare == false){
				lightUp(ballLeft, ballTop);
				if(tokenLightOn == true){
					tokenLightUp();
				}
			}
		}
		
		if(tokenFinder){
			findTokens();
			tokenFinder = false;
		}
    }
	
	
	
	function checkCollision(){		
				
		var collision = false;
		
		//LEFT
		if(speedX < 0){
			if(checkColor(ballLeft-(speedX*-1), ballTop+(ballRadius/2), (speedX*-1), ballRadius)){
				if(speedX < (bounceSensitivity*-1)){
					speedX = speedX * bounceSpeedDimish;
				}
				else{
					speedX = 0;
				}
				collision = true;
			}
		}
		//RIGHT
		else if(speedX > 0){
			if(checkColor(ballRight, ballTop+(ballRadius/2), speedX, ballRadius)){
				if(speedX > bounceSensitivity){
					speedX = speedX * bounceSpeedDimish;
				}
				else{
					speedX = 0;
				}
				collision = true;
			}
		}
		//TOP
		if(speedY < 0){
			if(checkColor(ballLeft+(ballRadius/2), ballTop-(speedY*-1), ballRadius, (speedY*-1))){
				if(speedY < (bounceSensitivity*-1)){
					speedY = speedY * bounceSpeedDimish;
				}
				else{
					speedY = 0;
				}
				collision = true;
			}
		}
		//BOTTOM
		else if(speedY > 0){
			if(checkColor(ballLeft+(ballRadius/2), ballBottom, ballRadius, speedY)){
				if(speedY > bounceSensitivity){
					speedY = speedY * bounceSpeedDimish;
				}
				else{
					speedY = 0;
				}
				collision = true;
			}
		}	
		
		if(collision == false){		
			//TOP-LEFT
			if(speedX <= 0 && speedY <= 0){
				if(checkColor(ballLeft-(speedX*-1), ballTop-(speedY*-1), (speedX*-1)+(ballRadius/2), (speedY*-1)+(ballRadius/2))){
					//window.alert("TOP-LEFT");
					speedY = speedY * -1;
					speedX = speedX * -1;
				}
			}
			//BOTTOM-LEFT
			else if(speedX <= 0 && speedY >= 0){
				if(checkColor(ballLeft-(speedX*-1), ballBottom-(ballRadius/2), (speedX*-1)+(ballRadius/2), speedY+(ballRadius/2))){
					//window.alert("BOTTOM-LEFT");
					speedY = speedY * -1;
					speedX = speedX * -1;
				}		
			}
			//TOP-RIGHT
			else if(speedX >= 0 && speedY <= 0){
				if(checkColor(ballRight-(ballRadius/2), ballTop-(speedY*-1), speedX+(ballRadius/2), (speedY*-1)+(ballRadius/2))){
					//window.alert("TOP-RIGHT");
					speedY = speedY * -1;
					speedX = speedX * -1;
				}
			}
			//BOTTOM-RIGHT
			else if(speedX >= 0 && speedY >= 0){
				if(checkColor(ballRight-(ballRadius/2), ballBottom-(ballRadius/2), speedX+(ballRadius/2), speedY+(ballRadius/2))){
					//window.alert("BOTTOM-RIGHT");
					speedY = speedY * -1;
					speedX = speedX * -1;
				}
			}
		}
		
	}
	
	function checkColor(x, y, width, height) {
	//Create image of the zone that the ball would potentially be moving to
		var imgData = context.getImageData(x,y,width,height);
		var pixels = imgData.data;
		
		//check these pixels
		for (var i = 0; n = pixels.length, i < n; i += 4) {
			var red = pixels[i];
			var green = pixels[i+1];
			var blue = pixels[i+2];
			var alpha = pixels[i+3];
			
			//Look for black
			if (red == 0 && green == 0 && blue == 0){
			//Play SoundCollision when colliding with black pixels
				//SoundCollision();
				return true;
			}
			
			//Look for Red
			if (red > 200 && green == 0 && blue == 0){
				SoundFinish();
				Goal();
				return;
			}
			//Look for Green
			if (red == 0 && green > 180 && blue == 0){
				return;
			}
			//Look for Blue
			if (red == 0 && green == 0 && blue > 200){
				SoundToken();
				if(nightmare == false){
					tokenTimer = 0;
					tokenLightOn = true;
					tokenLightX = ballLeft + ballRadius;				
					tokenLightY = ballTop + ballRadius;
				}
				return;
				
			}
		}		
		return false;
	}
	
		function randomInt(min,max)	{
		return Math.floor(Math.random()*(max-min+1)+min);
	}
	
	function pickMaze() {
	
		if(lsSize == "random" || lsSize == "undefined"){		
			window.alert("Picking random maze");
			var randomNr = randomInt(1,3);
		}
		
		if(randomNr==1 || lsSize=="small"){
			mazeName = "small";
			skinName = "brushed";
		}
		else if(randomNr==2 || lsSize=="medium"){
			mazeName = "medium";
			skinName = "skulls";			
		}
		else if(randomNr==3 || lsSize=="big"){
			mazeName = "big";
			skinName = "kitty";
		}			
	}
	
	function pickMode() {
		if(lsMode == "random" || lsMode == "undefined"){
			window.alert("Picking random mode");
			var randomNr = randomInt(1,3);
		}
		
		if(randomNr==1 || lsMode=="visible"){
			darkness = false;
		}		
		else if(randomNr==2 || lsMode=="trace"){
			setDarkness();
			darkness = true;
		}		
		else if(randomNr==3 || lsMode=="glowing"){
			setDarkness();
			darkness = true;
		}		
		else if(randomNr==4 || lsMode=="dark"){
			darkness = true;
			nightmare = true;
			
		}
		
		if(darkness){
			var divBall = document.getElementById('ball');
			divBall.style.opacity = '0';
		}
	
	}
	
	
	function findTokens(){
		var tokens = 0;
		var ftX;
		var ftY = 0;
		var ftWidth = (windowHeight/18);
		var ftHeight = windowHeight;
		for(var j = 0; j < 18; j += 1){
			ftX = (((windowWidth/2)-(windowHeight/2)) + (ftWidth * j));
			var imgData = context.getImageData(ftX, ftY, (ftWidth*0.8), ftHeight);
			var pixels = imgData.data;
			for (var i = 0; n = pixels.length, i < n; i += 4) {
				var red = pixels[i];
				var green = pixels[i+1];
				var blue = pixels[i+2];
				var alpha = pixels[i+3];
				
				//Look for Blue
				if (red == 0 && green == 0 && blue > 200){
					//SoundToken();
					tokens += 1;
					break;
				}
			}
		}		
		//window.alert("Tokens found: "+tokens);
	}
	

    // onError: Failed to get the acceleration
    //
    function onError() {
        alert('onError!');
    }
    
    //what happens in goal
    function Goal(){
    	//just a reload :P
		window.alert("You win!");
    	window.location.reload();
    }
	
	//Play mp3 file
	//function SoundCollision() {
	//var audio = new Audio('/android_asset/www/punch.ogg');
	// 0735.ogg was downloaded from: http://www.bigsoundbank.com/sound-0735-water-drum-1.html made by faeli83
	// punch.mp3 (also converted to ogg) was downloaded from: http://soundbible.com/2069-Realistic-Punch.html made by Mark DiAngelo
	//audio.play();
	//}
	
	function SoundCollision() {
	soundfile = new Media("/android_asset/www/punch.ogg",
	// punch.mp3 (also converted to ogg) was downloaded from: http://soundbible.com/2069-Realistic-Punch.html made by Mark DiAngelo
        function() {
            //alert("playAudio():Audio Success");
			//alert on succes
        },
            function(err) {
                alert(err);
				//alert on error
        }
        );
      soundfile.play();
	}
	function SoundToken() {
	soundfile = new Media("/android_asset/www/mirror.ogg",
	// punch.mp3 (also converted to ogg) was downloaded from: http://soundbible.com/2069-Realistic-Punch.html made by Mark DiAngelo
        function() {
            //alert("playAudio():Audio Success");
			//alert on succes
        },
            function(err) {
                alert(err);
				//alert on error
        }
        );
      soundfile.play();
	}
	
	function SoundFinish() {
	soundfile = new Media("/android_asset/www/chains.ogg",
	// punch.mp3 (also converted to ogg) was downloaded from: http://soundbible.com/2069-Realistic-Punch.html made by Mark DiAngelo
        function() {
            //alert("playAudio():Audio Success");
			//alert on succes
        },
            function(err) {
                alert(err);
				//alert on error
        }
        );
      soundfile.play();
	}
	//Testbutton in game.html
	function buttonClicked()
	{
	SoundCollision();
	}
	
	
	function setDarkness(){
		//Set up the darknessCanvas
		darknessCanvas = document.getElementById("darkness");
		darknessContext = darknessCanvas.getContext("2d");
		darknessCanvas.width = windowWidth;
		darknessCanvas.height = windowHeight;
		darknessCanvas.style.top = "0px";
		darknessCanvas.style.left = "0px";
		darknessContext.globalAlpha = 1;
		darknessContext.fillStyle = "rgb(0, 0, 0)";
		darknessContext.fillRect(((windowWidth/2)-(windowHeight/2)),0, windowHeight, windowHeight);
		darknessContext.globalCompositeOperation = "destination-out";	
	}
	
	function lightUp(x,y){
		if(lsMode=="glowing"){
			setDarkness();
		}
		x = x + ballRadius;
		y = y + ballRadius;
		var grd = darknessContext.createRadialGradient(x,y,1,x,y,25);
		
		/*grd.addColorStop(0, "transparent");
		grd.addColorStop(0.3, "rgba(255,255,255,.6)"); 
		grd.addColorStop(0.7, "rgba(255,255,255,.6)"); 
		grd.addColorStop(1, "transparent"); */
		grd.addColorStop(0, "rgba(255,255,255, 1)");
		grd.addColorStop(0.6, "rgba(255,255,255, 1)");
		grd.addColorStop(1, "transparent");
		
		darknessContext.fillStyle = grd;
		darknessContext.fillRect(0,0,windowWidth,windowHeight);
	}
		
	function tokenLightUp(){
		//document.getElementById('test').innerHTML = tokenTimer;
		tokenTimer += 1;
		x = tokenLightX;
		y = tokenLightY;
		var grd = darknessContext.createRadialGradient(x,y,1,x,y,90);
		
		grd.addColorStop(0, "rgba(255,255,255, 1)");
		grd.addColorStop(0.6, "rgba(255,255,255, 1)");
		grd.addColorStop(1, "transparent");
		
		darknessContext.fillStyle = grd;
		darknessContext.fillRect(0,0,windowWidth,windowHeight);
		
		if(tokenTimer >= tokenLightTime){
			tokenLightOn = false;
			tokenTimer = 0;
		}
	}		
	
	function clearBall(x,y){
		symbolsContext.clearRect(x-5, y-5, ballSize+10, ballSize+10);
	}
	
	function drawCircle(x,y){
		x = x + ballRadius;
		y = y + ballRadius;
		//symbolsContext.clearRect(((windowWidth/2)-(windowHeight/2)),0, windowHeight, windowHeight);
		symbolsContext.beginPath();
		symbolsContext.arc(x,y, ballRadius, 0, Math.PI * 2, false);
		symbolsContext.closePath();
		if(lsColor == "undefined"){
			symbolsContext.fillStyle = "red";
		}
		else{
			symbolsContext.fillStyle = lsColor;
		}
		symbolsContext.fill();
	}
	
	function ballSetup(size){
		//BALL DEFINITIONS
		var bs = document.getElementById('ball');
		//var size = "12";
		var startLeft = ((windowWidth/2)-(windowHeight/2)) + 10;
		var startTop = 10;		
				
		//BALL SETUP
		bs.style.left = startLeft + 'px';
		bs.style.Top = startTop + 'px';
		bs.style.width = size + 'px';
		bs.style.height = size + 'px';
		bs.style.backgroundColor = lsColor;		
		
		//FETCH VALUES
		ball = $("#ball");		
		ballSize = ball.height();
		ballRadius = ballSize/2;		
		ballPosition = ball.position();
		ballLeft = ballPosition.left;
		ballRight = ballPosition.left + ballSize;
		ballTop = ballPosition.top;
		ballBottom = ballPosition.top + ballSize;		
	}
	
	function symbolsSetup(){
		//SETUP SYMBOLS LAYER FOR ICONS (BALL, TOKENS..)
		symbolsCanvas = document.getElementById("symbols");
		symbolsContext = symbolsCanvas.getContext("2d");
		symbolsCanvas.width = windowWidth;
		symbolsCanvas.height = windowHeight;
		symbolsCanvas.style.top = "0px";
		symbolsCanvas.style.left = "0px";	
	}
	
	function skinSetup(){
	//SKIN DEFINITIONS
		var skinSetup = document.getElementById('skin');
		var skinSize = windowHeight;
		var skinLeft = (windowWidth/2)-(windowHeight/2);
		
		//SKIN SETUP
		skinSetup.style.left = skinLeft + 'px';
		skinSetup.style.width = skinSize + 'px';
		skinSetup.style.height = skinSize + 'px';
		skinSetup.src = 'skins/'+skinName+'.png';
	}
	

