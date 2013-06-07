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

	function distance($lat1, $lon1, $lat2, $lon2) {
	  $theta = $lon1 - $lon2;
	  $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
	  $dist = acos($dist);
	  $dist = rad2deg($dist);
	  $miles = $dist * 60 * 1.1515;
		return $miles;
	}
	
	$sql = 'SELECT * FROM Fountain_Location;' ;
	
	
	$con = new mysqli($host, $user, $pass, $dbase);
	if($con->connect_errno > 0){
		die('Unable to connect to database [' . $con->connect_error . ']');
	}

	if(!$result = $con->query($sql)){
			die('There was an error running the query [' . $con->error . ']');
		}


	while($row = $result->fetch_assoc()){
		$fid = $row['Fount_ID'];
		$f_longitude = $row['Longitude'];
		$f_latitude = $row['Latitude'];
		if (distance($latitude, $longitude, $f_latitude, $f_longitude) <= 1) {
		
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
			
			echo $fid.','.$f_latitude.','.$f_longitude.','.$numY.','.$numN.';';
		}
	}





	

?>