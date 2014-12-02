	//Define global variables
		
		//Settings
		var labyrinthSize = "small";
		var labyrinthMode = "visible";
		
		//Maze definitions
		var canvas;
		var context;
		var mazeName = "small.jpg";
		
		//Ball movement definitions
		var speedX = 0;
		var speedY = 0;
		var speedUnit = 0.5;
		var maxSpeed = 6;
		
		/*
		//Collision definitions
		var collisionLeft = 0;
		var collisionRight = 0;
		var collisionTop = 0;
		var collisionBottom = 0;
	
		//Boundary definitions
		var boundaryLeft = 0;
		var boundaryRight = $(window).width();
		var boundaryTop = 0;
		var boundaryBottom = $(window).height();
		*/
		
		//Ball definitions
		var ball = null;
		var ballPosition = null;
		var ballLeft = 0;
		var ballRight = 0;
		var ballTop = 0;
		var ballBottom = 0;
		var ballRadius = 0;
		
		//Acceleration definitions
		var accX = 0;
		var accY = 0;
		

	
    // The watch id references the current `watchAcceleration`
    var watchID = null;

    // Wait for Cordova to load
    //
    document.addEventListener("deviceready", onDeviceReady, false);

    // Cordova is ready
    //
    function onDeviceReady() {
		//Set up the canvas
		canvas = document.getElementById("canvas");
		context = canvas.getContext("2d");
		
		//Draw the maze background
		drawMaze(mazeName);
				
        startWatch();
    }

	//Drawing the maze
	function drawMaze(mazeFile){
		var imgMaze = new Image();
		imgMaze.onload = function() {
			//Resize the canvas to match the mace picture
			imgMaze.width = $(window).width();
			imgMaze.height = $(window).height();
			canvas.width = imgMaze.width;
			canvas.height = imgMaze.height;
			
			//Draw the maze
			context.drawImage(imgMaze, 0,0);		
		}
		imgMaze.src = mazeFile;
	}
    // Start watching the acceleration
    //
    function startWatch() {

        // Update acceleration every 0,003 seconds
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
       	
		//Fetch BALL object
		ball = $("#ball");
		
		//Fetch BALL POSITION
		ballPosition = ball.position();
		ballLeft = ballPosition.left;
		ballRight = ballPosition.left + ball.width();
		ballTop = ballPosition.top;
		ballBottom = ballPosition.top + ball.height();
		ballRadius = ball.height()/2;
		
		//Fetch ACCELERATION values
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
		
		//Check collision
		checkCollision();		
		//MOVE BALL
		ball.velocity({top:"+="+speedY, left:"+="+speedX}, {duration: 0.1});
    }
	
	
	function checkCollision(){
		
		/*
		//SCREEN collision		
		//Check LEFT collision
		if(ballLeft <= boundaryLeft && accX <= 0){
			//Check IF speed is high enough to go through boundary
			if((ballLeft + speedX) < boundaryLeft){
				speedX = ballLeft * -1;
			}
			else{
				speedX = 0;
			}
		}
		//Check RIGHT collision
		else if(ballRight >= boundaryRight  && accX >= 0){
			//Check IF speed is high enough to go through boundary
			if((ballRight + speedX) > boundaryRight){
				speedX = boundaryRight - ballRight;
			}
			else{
				speedX = 0;
			}
		}
		//Check TOP collision
		if(ballTop <= boundaryTop && accY <= 0){
			//Check IF speed is high enough to go through boundary
			if((ballTop + speedY) < boundaryTop){
				speedY = ballTop * -1;
			}
			else{
				speedY = 0;
			}
		}
		//Check BOTTOM collision
		else if(ballBottom >= boundaryBottom && accY >= 0){
			//Check IF speed is high enough to go through boundary
			if((ballBottom + speedY) > boundaryBottom){
				speedY = boundaryBottom - ballBottom;
			}
			else{
				speedY = 0;
			}
		}
		*/
		//WALL collision
		//LEFT
		if(speedX < 0){
			if(checkColor(ballLeft-speedX, ballTop+ballRadius, speedX, 5)){
				speedX = speedX * -1;
			}
		}
		//RIGHT
		else if(speedX > 0){
			if(checkColor(ballRight, ballTop+ballRadius, speedX, 5)){
				speedX = speedX * -1;
			}
		}
		//TOP
		if(speedY < 0){
			if(checkColor(ballLeft+ballRadius, ballTop-speedY, 5, speedY)){
				speedY = speedY * -1;
			}
		}
		//BOTTOM
		else if(speedY > 0){
			if(checkColor(ballLeft+ballRadius, ballBottom, 5, speedY)){
				speedY = speedY * -1;
			}
		}
		
	}
	
	function checkColor(x, y, width, height) {
	//Grab the pixels from the ball
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
				SoundCollision();
				return true;
			}
			
			//Look for Red
			if (red > 200 && green == 0 && blue == 0){
				window.alert("Red!");
				Goal();
				return;
			}
			//Look for Green
			if (red == 0 && green > 180 && blue == 0){
				window.alert("green!");
				return;
			}
		}		
		return false;
	}
    // onError: Failed to get the acceleration
    //
    function onError() {
        alert('onError!');
    }
    
    //what happens in goal
    function Goal(){
    	//just a reload :P
    	window.location.reload();
    }
	
	//Play mp3 file
	function SoundCollision() {
	var audio = new Audio('punch.mp3');
	// punch.mp3 was downloaded from: http://soundbible.com/2069-Realistic-Punch.html made by Mark DiAngelo
	audio.play();
	}

	//Testbutton in game.html
	function buttonClicked()
	{
	SoundCollision();
	}
