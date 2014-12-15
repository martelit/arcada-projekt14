		//Window size definitions
	var windowWidth = $(window).width();
	var windowHeight = $(window).height();
	//Watch (for accelerometer) definition
    var watchID = null;
	//Game mode definitions
	var mode = {
		darkness: false,
		nightmare: false,		
	}
	//Darkness game modes definitions
	var darkness = {
		c: null,
		ctx: null		
	}
	//Token definitions
	var token = {
		timer: 0,
		lightTime: 150,
		lightOn: false,
		lightRadius: 90,
		x: 0,
		y: 0
	}
	//Gameboard definitions
	var gameboard = {
		left: ((windowWidth/2)-(windowHeight/2)),
		right: ((windowWidth/2)-(windowHeight/2)) + windowHeight,
		width: windowHeight,
		height: windowHeight	
	}
	//Background layer definitions
	var background = {
		c: null,
		ctx: null,
		name: "null",
	}
	//Skin layer definitions
	var skin = {
		name: "null",
	}
	//Symbol layer definitions
	var symbols = {
		c: null,
		ctx: null
	}
	//Settings definitions
	var settings = {
		size: localStorage.size,
		mode: localStorage.mode,
		color: localStorage.color		
	}
	//Ball speed definitions
	var speed = {
		x: 0,
		y: 0,
		unit: 0.2,
		max: 3
	}
	//Ball bounce definitions
	var bounce = {
		sensitivity: speed.unit * 1.01,
		dimish: -0.7
	}
	//Ball definitions
	var ball = {
		c: null,
		ctx: null,
		obj: null,
		position: null,
		left: 0,
		right: 0,
		top: 0,
		bottom: 0,
		radius: 0,
		size: 0,
		lightRadius: 25,
	}
	//Acceleration definitions
	var acc = {
		x: 0,
		y: 0
	}

    // Wait for Cordova to load
    document.addEventListener("deviceready", onDeviceReady, false);
	
    // Cordova is ready
    function onDeviceReady() {		
		//Load game mode and map + skin based on settings
		pickMaze();		
		pickMode();		
		
		//Background (that is used for collision detection) map set up		
		mapSetup("maps/"+background.name+".png");		
		
		//Graphical skin layer set up
		skinSetup();
		
		//Symbol layer set up
		symbolsSetup();
		
		//Load token skins to symbolcanvas
		window.setTimeout(function(){findTokens();}, 1000);
		
		//BALL SETUP, ballSize as parameter
		window.setTimeout(function(){ballSetup(12);}, 1000);
		
		//Start accelerometer
		startWatch();
    }

    // Start watching the acceleration
    function startWatch() {
		//1000 (1s) / 30 = 30FPS
        var options = { frequency: (1000 / 30) };
        watchID = navigator.accelerometer.watchAcceleration(onSuccess, onError, options);
    }

    // Stop watching the acceleration
    function stopWatch() {
        if (watchID) {
            navigator.accelerometer.clearWatch(watchID);
            watchID = null;
        }
    }
	
    // onSuccess: Get a snapshot of the current acceleration
	// MAIN THREAD
    function onSuccess(acceleration) { 
		//Fetch ACCELERATION values, X=Y && Y=X due to forced landscape
		acc.x = acceleration.y;
		acc.y = acceleration.x;
		
		//UPDATES speed variables based on acceleration
		updateMovement();
		
		//ALL MODES, check for collision
		checkCollision();
		
		//Ball movement
		drawBall();
    }
	
	// onError: Failed to get the acceleration
    function onError() {
        alert('onError!');
    }
	
	//USE velocity for ball animation
	function animateBall(){
		ball.obj.velocity({top:"+="+speed.y, left:"+="+speed.x}, {duration: 0.1});
	}
	
	//Clear old ball, update variables and draw the new ball
	//DARKMODES: call for lightUp on ball coordinates and tokenLight if its token.lightOn == true
	function drawBall(){
		//Clear old ball
		clearBall(ball.left,ball.top);
		
		//Update ball location
		ball.left += speed.x;
		ball.right = ball.left + ball.size;
		ball.top += speed.y;
		ball.bottom = ball.top + ball.size;
		
		//Draw the new ball
		drawCircle(ball.left, ball.top);
		
		//If darkness is true and nightmare is false, lightup ball and check if token should be lit
		if(mode.darkness && mode.nightmare == false){
			lightUp(ball.left, ball.top);
			if(token.lightOn && settings.mode == "glowing"){
				tokenLightUp();
			}
		}
	}
	
	//UPDATE MOVEMENT on basis of the current acceleration
	function updateMovement(){
		//MOVE TO RIGHT, acc.x is POSITIVE
		if(acc.x > 1){
			//Check if ball is moving at max speed
			if(speed.x <= speed.max){
				//Add twice the speed.unit if the ball is moving in the opposite direction (faster turnarounds)
				if(speed.x <= 0){
					speed.x += speed.unit*2;
				}
				//Else add one speed unit
				else{
					speed.x += speed.unit;
				}
			}			
		}
		//MOVE TO LEFT, acc.x is NEGATIVE, refer to MOVE TO RIGHT for comments
		else if(acc.x < -1){
			if(speed.x >= -speed.max){
				if(speed.x >= 0){
					speed.x += -speed.unit*2;
				}
				else{
					speed.x += -speed.unit;
				}
			}
		}
		//SLOW DOWN HORIZONTAL, acc.x is NEUTRAL
		else{
			//If current speed is higher than a single speed unit, cut it down by half a speed unit
			if(speed.x > speed.unit){
				speed.x += -speed.unit/2;
			}
			//If current speed is higher than a single speed unit, cut it down by half a speed unit
			else if(speed.x < -speed.unit){
				speed.x += speed.unit/2;
			}
			//If current speed is less than a single speed unit set the speed to 0. To avoid endless bouncing
			else{
				speed.x = 0;
			}
		}
		//MOVE DOWNWARDS, acc.y is POSITIVE, refer to MOVE TO RIGHT for comments
		if(acc.y > 1){
			if(speed.y <= speed.max){
				if(speed.y <= 0){
					speed.y += speed.unit*2;
				}
				else{
					speed.y += speed.unit;
				}
			}
		}
		//MOVE UPWARDS, acc.y is NEGATIVE, refer to MOVE TO RIGHT for comments
		else if(acc.y < -1){
			if(speed.y >= -speed.max){
				if(speed.y >= 0){
					speed.y += -speed.unit*2;
				}
				else{
					speed.y += -speed.unit;
				}
			}
		}
		//SLOW DOWN VERTICAL, acc.y is NEUTRAL, refer to SLOW DOWN HORIZONTAL for comments
		else{
			if(speed.y > speed.unit){
				speed.y += -speed.unit/2;
			}
			else if(speed.y < -speed.unit){
				speed.y += speed.unit/2;
			}
			else{
				speed.y = 0;
			}
		}	
	}
	
	//CHECK FOR COLLISION
	function checkCollision(){		
				
		var collision = false;
		
		//Send field of pixels to checkColor function. Field is as small as possible based on the speed.
		//Field created like follows, checkColor(left coordinate, top coordinate, width, height).
		//Field + ball demonstration below...
	//				 _____
	//		 ______	/     \
	//		*	   |       |
	//		|______|       |
	//		        \_____/
		
		//LEFT
		if(speed.x < 0){
			//Send the field to checkColor, returns TRUE if there's an collision
			if(checkColor(ball.left-(speed.x*-1), ball.top+(ball.radius/2), (speed.x*-1), ball.radius)){
				//Check if current speed is higher than the bounce sensitivity
				if(speed.x < (bounce.sensitivity*-1)){
					//Bounce ball back and apply speed dimish
					speed.x = speed.x * bounce.dimish;
				}
				//If speed is lower than the bounce sensitivity, set speed to 0 (no bounce)
				else{
					
					speed.x = 0;
				}
				//Set collision to true 
				collision = true;
			}
		}
		//RIGHT, refer to LEFT for comments
		else if(speed.x > 0){
			if(checkColor(ball.right, ball.top+(ball.radius/2), speed.x, ball.radius)){
				if(speed.x > bounce.sensitivity){
					speed.x = speed.x * bounce.dimish;
				}
				else{
					speed.x = 0;
				}
				collision = true;
			}
		}
		//TOP, refer to LEFT for comments
		if(speed.y < 0){
			if(checkColor(ball.left+(ball.radius/2), ball.top-(speed.y*-1), ball.radius, (speed.y*-1))){
				if(speed.y < (bounce.sensitivity*-1)){
					speed.y = speed.y * bounce.dimish;
				}
				else{
					speed.y = 0;
				}
				collision = true;
			}
		}
		//BOTTOM, refer to LEFT for comments
		else if(speed.y > 0){
			if(checkColor(ball.left+(ball.radius/2), ball.bottom, ball.radius, speed.y)){
				if(speed.y > bounce.sensitivity){
					speed.y = speed.y * bounce.dimish;
				}
				else{
					speed.y = 0;
				}
				collision = true;
			}
		}	
		
		//If there was no collision on LEFT,RIGHT,TOP or DOWN we'll have to check TOP-LEFT,BOTTOM-LEFT,TOP-RIGHT and BOTTOM-RIGHT
		if(collision == false){		
			//TOP-LEFT, refer to LEFT for comments
			if(speed.x <= 0 && speed.y <= 0){
				if(checkColor(ball.left-(speed.x*-1), ball.top-(speed.y*-1), (speed.x*-1)+(ball.radius/2), (speed.y*-1)+(ball.radius/2))){
					speed.y = speed.y * -1;
					speed.x = speed.x * -1;
				}
			}
			//BOTTOM-LEFT, refer to LEFT for comments
			else if(speed.x <= 0 && speed.y >= 0){
				if(checkColor(ball.left-(speed.x*-1), ball.bottom-(ball.radius/2), (speed.x*-1)+(ball.radius/2), speed.y+(ball.radius/2))){
					speed.y = speed.y * -1;
					speed.x = speed.x * -1;
				}		
			}
			//TOP-RIGHT, refer to LEFT for comments
			else if(speed.x >= 0 && speed.y <= 0){
				if(checkColor(ball.right-(ball.radius/2), ball.top-(speed.y*-1), speed.x+(ball.radius/2), (speed.y*-1)+(ball.radius/2))){
					speed.y = speed.y * -1;
					speed.x = speed.x * -1;
				}
			}
			//BOTTOM-RIGHT, refer to LEFT for comments
			else if(speed.x >= 0 && speed.y >= 0){
				if(checkColor(ball.right-(ball.radius/2), ball.bottom-(ball.radius/2), speed.x+(ball.radius/2), speed.y+(ball.radius/2))){
					speed.y = speed.y * -1;
					speed.x = speed.x * -1;
				}
			}
		}		
	}
	
	//Creates field and checks color of pixels inside of it
	function checkColor(x, y, width, height) {
	//Create image of the field that the ball would potentially be moving to
		var imgData = background.ctx.getImageData(x,y,width,height);
		var pixels = imgData.data;
		
		//check these pixels
		for (var i = 0; n = pixels.length, i < n; i += 4) {
			var red = pixels[i];
			var green = pixels[i+1];
			var blue = pixels[i+2];
			var alpha = pixels[i+3];
			
			//WALL, look for black
			if (red == 0 && green == 0 && blue == 0){
				return true;
			}			
			//GOAL, look for red
			else if (red > 200 && green == 0 && blue == 0){
				SoundFinish();
				Goal();
				break;
			}
			//TOKEN, look for Blue, token.timer variable is there to avoid multiple hits
			else if (red == 0 && green == 0 && blue > 200 && (token.timer > 20 || token.timer === 0)){
				SoundToken();
				if(mode.darkness == true && mode.nightmare == false){
					token.timer = 0;
					token.lightOn = true;
					token.x = ball.left + ball.radius;				
					token.y = ball.top + ball.radius;
					if(settings.mode == "glowing"){
						repaintDarkness(token.x, token.y, token.lightRadius);
					}
					tokenLightUp();
				}
				return;			
			}
		}		
		return false;
	}
	
	//Create random int between min and max
	function randomInt(min,max)	{
		return Math.floor(Math.random()*(max-min+1)+min);
	}
	
	//Set background and skin variables based on settings
	function pickMaze() {	
		if(settings.size == "random" || settings.size == "undefined"){		
			window.alert("Picking random maze");
			var randomNr = randomInt(1,3);
		}
		
		if(randomNr==1 || settings.size=="small"){
			background.name = "small";
			skin.name = "brushed";
		}
		else if(randomNr==2 || settings.size=="medium"){
			background.name = "medium";
			skin.name = "skulls";			
		}
		else if(randomNr==3 || settings.size=="big"){
			background.name = "big";
			skin.name = "kitty";
		}			
	}
	//Set game mode based on settings
	function pickMode() {
		if(settings.mode == "random" || settings.mode == "undefined"){
			window.alert("Picking random mode");
			var randomNr = randomInt(1,3);
		}
		
		if(randomNr==1 || settings.mode=="visible"){
			mode.darkness = false;
		}		
		else if(randomNr==2 || settings.mode=="trace"){
			setDarkness();
			mode.darkness = true;
		}		
		else if(randomNr==3 || settings.mode=="glowing"){
			setDarkness();
			mode.darkness = true;
		}		
		else if(randomNr==4 || settings.mode=="dark"){
			mode.darkness = true;
			mode.nightmare = true;			
		}
		
		if(mode.darkness){
			var divBall = document.getElementById('ball');
			divBall.style.opacity = '0';
		}
	
	}
	
	//Drawing the background
	function mapSetup(mazeFile){
		//Set up the canvas
		var imgMaze = new Image();
		imgMaze.onload = function() {	
			background.c = document.getElementById("canvas");
			background.ctx = background.c.getContext("2d");	
			background.c.width = windowWidth;
			background.c.height = windowHeight;	
			//Draw the maze
			background.ctx.drawImage(imgMaze, gameboard.left,0, gameboard.width, gameboard.height);		
		}
		imgMaze.src = mazeFile;
	}
	//Drawing first ball
	function ballSetup(size){
		ball.c = document.getElementById("cBall");
		ball.ctx = ball.c.getContext("2d");
		ball.c.width = windowWidth;
		ball.c.height = windowHeight;
		ball.c.style.top = "0px";
		ball.c.style.left = "0px";
		
		ball.size = size;
		ball.radius = ball.size/2;
		ball.right = ball.left + ball.size;
		ball.bottom = ball.top + ball.size;
		
		drawCircle(ball.left, ball.top);
		/*
		//BALL DEFINITIONS
		var bs = document.getElementById('ball');
		var startLeft = ((windowWidth/2)-(windowHeight/2)) + 10;
		var startTop = 10;		
				
		//BALL SETUP
		bs.style.left = ball.left + 'px';
		bs.style.Top = ball.top + 'px';
		bs.style.width = size + 'px';
		bs.style.height = size + 'px';
		bs.style.backgroundColor = settings.color;		
		
		//FETCH VALUES
		ball.obj = $("#ball");		
		ball.size = ball.obj.height();
		ball.radius = ball.size/2;		
		ball.position = ball.obj.position();
		ball.left = ball.position.left;
		ball.right = ball.position.left + ball.size;
		ball.top = ball.position.top;
		ball.bottom = ball.position.top + ball.size;*/
	}
	
	//SETUP SYMBOLS LAYER FOR ICONS (TOKENS & FINISH)
	function symbolsSetup(){
		symbols.c = document.getElementById("symbols");
		symbols.ctx = symbols.c.getContext("2d");
		symbols.c.width = windowWidth;
		symbols.c.height = windowHeight;
		symbols.c.style.top = "0px";
		symbols.c.style.left = "0px";	
	}
	
	//Skin layer set up
	function skinSetup(){
		//SKIN DEFINITIONS
		var skinSetup = document.getElementById('skin');
		var skinSize = gameboard.width;
		var skinLeft = gameboard.left;
		
		//SKIN SETUP
		skinSetup.style.left = skinLeft + 'px';
		skinSetup.style.width = skinSize + 'px';
		skinSetup.style.height = skinSize + 'px';
		skinSetup.src = 'skins/'+skin.name+'.png';
	}
	
	//Find tokens using checkColor
	function findTokens(){		
		window.alert("Loading...");
		var tokens = 0;
		var x = gameboard.left;
		var width = (gameboard.width/18);
		
		//X
		for(var j = x; j < (gameboard.right); j += 1){
			//Y
			for(var k = 1; k < gameboard.height; k += 1){
				var imgData = background.ctx.getImageData(j, k, 1, 1);
				var pixels = imgData.data;
				for (var i = 0; n = pixels.length, i < n; i += 4) {
					var red = pixels[i];
					var green = pixels[i+1];
					var blue = pixels[i+2];
					var alpha = pixels[i+3];
					
					//Look for Blue
					if (red == 0 && green == 0 && blue > 200){
						tokens += 1;
						loadSymbol(j,k,"token");
						j += width;
						break;
					}
				}
			}
		}
		findStart();
		findFinish();
		window.alert("Tokens found: "+tokens);
		//Show map for 3 seconds if nightmare -game mode is selected
		if(mode.nightmare){
			window.setTimeout(function(){setDarkness();}, 3000);
		}
	}
	//Find start using checkColor
	function findStart(){
		var x = gameboard.left;
		var width = (gameboard.width/18);
		
		//X
		for(var j = x; j < (gameboard.right); j += 1){
			//Y
			for(var k = 1; k < gameboard.width; k += 1){
				var imgData = background.ctx.getImageData(j, k, 1, 1);
				var pixels = imgData.data;
				for (var i = 0; n = pixels.length, i < n; i += 4) {
					var red = pixels[i];
					var green = pixels[i+1];
					var blue = pixels[i+2];
					var alpha = pixels[i+3];
					
					//Look for Green
					if (red == 0 && green > 180 && blue == 0){
						ball.left = j;
						ball.top = k;
						j = (gameboard.right);
						break;
					}
				}
			}
		}
	}
	//Find finish using checkColor
	function findFinish(){
		var x = gameboard.left;		
		//X
		for(var j = (gameboard.right); j > x; j -= 1){
			//Y
			for(var k = 1; k < gameboard.height; k += 1){
				var imgData = background.ctx.getImageData(j, k, 1, 1);
				var pixels = imgData.data;
				for (var i = 0; n = pixels.length, i < n; i += 4) {
					var red = pixels[i];
					var green = pixels[i+1];
					var blue = pixels[i+2];
					var alpha = pixels[i+3];
					
					//Look for Red
					if (red > 200 && green == 0 && blue == 0){
						loadSymbol(j-10,k-10,"finish");
						j = x;
						break;
					}
				}
			}
		}
	}
	//Loads symbol at coordinate
	function loadSymbol(x,y,name){
		var r = 10;
		var $img = $('<img>', { src: "skins/"+name+".png" });
		$img.load(function(){
			symbols.ctx.drawImage(this, x, y, r, r);
		});	
	}
	    
    //what happens in goal
    function Goal(){
		window.alert("You win!");
    	window.location.reload();
    }
	//Sets the darkness layer over skin
	function setDarkness(){
		darkness.c = document.getElementById("darkness");
		darkness.ctx = darkness.c.getContext("2d");
		darkness.c.width = windowWidth;
		darkness.c.height = windowHeight;
		darkness.c.style.top = "0px";
		darkness.c.style.left = "0px";
		darkness.ctx.globalAlpha = 1;		
		darkness.ctx.fillStyle = "rgb(0, 0, 0)";
		darkness.ctx.fillRect(gameboard.left,0, gameboard.width, gameboard.height);
		darkness.ctx.globalCompositeOperation = "destination-out";	
	}
	function repaintDarkness(x,y,r){
		r = r + 2;
		darkness.ctx.globalCompositeOperation = "source-over";
		darkness.ctx.arc(x,y,r, 0, Math.PI * 2, false);
		darkness.ctx.closePath();	
		darkness.ctx.fillStyle = "rgb(0, 0, 0)";
		darkness.ctx.fill();	
	}
	//Lights up at coordinate
	function lightUp(x,y){
		x = x + ball.radius;
		y = y + ball.radius;
		if(settings.mode=="glowing"){
			repaintDarkness(x,y,ball.lightRadius);
		}
		var grd = darkness.ctx.createRadialGradient(x,y,1,x,y,ball.lightRadius);
		
		grd.addColorStop(0, "rgba(255,255,255, 1)");
		grd.addColorStop(0.6, "rgba(255,255,255, 1)");
		grd.addColorStop(1, "transparent");
		
		darkness.ctx.globalCompositeOperation = "destination-out";
		darkness.ctx.beginPath();
		darkness.ctx.arc(x,y,ball.lightRadius, 0, Math.PI * 2, false);
		darkness.ctx.closePath();
		darkness.ctx.fillStyle = grd;
		darkness.ctx.fill();
	}
	//Lights up token, triggered by checkColor->blue
	function tokenLightUp(){
		x = token.x;
		y = token.y;
		
		var grd = darkness.ctx.createRadialGradient(x,y,1,x,y,token.lightRadius);
		
		grd.addColorStop(0, "rgba(255,255,255, 1)");
		grd.addColorStop(0.6, "rgba(255,255,255, 1)");
		grd.addColorStop(1, "transparent");
		
		darkness.ctx.globalCompositeOperation = "destination-out";
		darkness.ctx.beginPath();
		darkness.ctx.arc(x,y,token.lightRadius, 0, Math.PI * 2, false);
		darkness.ctx.closePath();
		darkness.ctx.fillStyle = grd;
		darkness.ctx.fill();						
		token.timer += 1;
		if(token.timer >= token.lightTime){
			token.lightOn = false;
			token.timer = 0;
			repaintDarkness(token.x, token.y, token.lightRadius);
		}
	}		
	//clears old ball
	function clearBall(x,y){
		ball.ctx.clearRect(x-5, y-5, ball.size+10, ball.size+10);
	}
	//draws new ball
	function drawCircle(x,y){
		x = x + ball.radius;
		y = y + ball.radius;
		ball.ctx.beginPath();
		ball.ctx.arc(x,y, ball.radius, 0, Math.PI * 2, false);
		ball.ctx.closePath();
		if(settings.color == "undefined"){
			ball.ctx.fillStyle = "red";
		}
		else{
			ball.ctx.fillStyle = settings.color;
		}
		ball.ctx.fill();
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
	
	

