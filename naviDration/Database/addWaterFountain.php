<?php
	$config = parse_ini_file(__DIR__ . '/../config.ini', true);
	$host = $config['database']['host'];
	$user = $config['database']['user'];
	$pass =  $config['database']['password'];
	$dbase = $config['database']['dbase'];
	$access_pass = $config['database']['access_pass'];
	$dbase_pass = $_GET['dbpass'];
	if ($dbase_pass != $access_pass){
		die('Invalid input! Password');
	}
	
	$longitude = $_GET['longitude'];
	$latitude = $_GET['latitude'];
	$rating = $_GET['rating'];
	$uid = $_GET['uid'];
	
	if (!($rating == 'Y' OR $rating == 'N')) {
		die('Invalid input! Rating');
	}
	
	$sql = 'INSERT INTO Fountain_Location (Longitude, Latitude) VALUES ('.$longitude.','.$latitude.");" ;

	$con = new mysqli($host, $user, $pass, $dbase);
	if($con->connect_errno > 0){
		die('Unable to connect to database [' . $con->connect_error . ']');
	}

	if(!$result = $con->query($sql)){
			die('There was an error running the query [' . $con->error . ']');
		}
		
		
	$sql = 'SELECT * FROM Fountain_Location WHERE Longitude='.$longitude.' AND Latitude='.$latitude.';' ;
	if(!$result = $con->query($sql)){
			die('There was an error running the query [' . $con->error . ']');
		}

	$row = $result->fetch_assoc();
	$fid = $row['Fount_ID'];
	
		
	$sql = 'INSERT INTO Fountain_Rating (Fount_ID, Rating, User_ID) VALUES ('.$fid.',\''.$rating.'\','.$uid.");" ;
	if(!$result = $con->query($sql)){
			die('There was an error running the query [' . $con->error . ']');
		}

	$sqlY = 'SELECT Count(Rating) AS numYes FROM Fountain_Rating WHERE Fount_ID='.$fid.' AND Rating=\'Y\';';
	$sqlN = 'SELECT Count(Rating) AS numNo FROM Fountain_Rating WHERE Fount_ID='.$fid.' AND Rating=\'N\';';

	if(!$resultY = $con->query($sqlY)){
	die('There was an error running the query [' . $con->error . ']');
	}

	if(!$resultN = $con->query($sqlN)){
	die('There was an error running the query [' . $con->error . ']');
	}

	$rowY = $resultY->fetch_assoc();
	$numY = $rowY['numYes'];

	$rowN = $resultN->fetch_assoc();
	$numN = $rowN['numNo'];

	echo $fid.','.$latitude.','.$longitude.','.$numY.','.$numN.";";	
	
			
?>
	