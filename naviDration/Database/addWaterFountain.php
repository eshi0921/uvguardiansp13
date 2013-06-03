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
	
	$longitude = $_GET['long'];
	$latitude = $_GET['lat'];
	
	
	$sql = 'INSERT INTO Fountain_Location (Longitude, Latitude) VALUES ('.$longitude.','.$latitude.");" ;


	$con = new mysqli($host, $user, $pass, $dbase);
	if($con->connect_errno > 0){
		die('Unable to connect to database [' . $con->connect_error . ']');
	}

	if(!$result = $con->query($sql)){
			die('There was an error running the query [' . $con->error . ']');
		}

	echo "Success";
			
?>
	