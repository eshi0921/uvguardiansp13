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
	
	
	$uid = $_GET['uid'];
	$fid = $_GET['fid'];
	$rating = $_GET['rating'];
	
	
	if (!($rating == 'Y' OR $rating == 'N')) {
		die('Invalid input! Rating');
	}
	$sql = 'SELECT COUNT(*) AS num FROM Fountain_Rating WHERE Fount_ID='.$fid.' AND User_ID='.$uid.';';
	
	$con = new mysqli($host, $user, $pass, $dbase);
	if($con->connect_errno > 0){
		die('Unable to connect to database [' . $con->connect_error . ']');
	}
	if(!$result = $con->query($sql)){
			die('There was an error running the query [' . $con->error . ']');
		}
	
	$row = $result->fetch_assoc();
	$num = $row['num'];
	
	if ($num == 1)
	{
		$sql = 'UPDATE Fountain_Rating SET Rating=\''.$rating.'\' WHERE Fount_ID='.$fid.' AND User_ID='.$uid.';';
	}
	else
	{
		$sql = 'INSERT INTO Fountain_Rating (Fount_ID, Rating, User_ID) VALUES ('.$fid.',\''.$rating.'\','.$uid.");" ;
		}
	
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
	
	echo $fid.','.$numY.','.$numN;
			
?>
	