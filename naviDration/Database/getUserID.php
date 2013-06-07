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
		
		$sql = 'SELECT * FROM MaxUserID;' ;
		$con = new mysqli($host, $user, $pass, $dbase);
		if (mysqli_connect_errno($con))
		  {
		  echo "Failed to connect to MySQL: " . mysqli_connect_error();
		  }
		
		if($con->connect_errno > 0){

			die('Unable to connect to database [' . $con->connect_error . ']');
		}

		if(!$result = $con->query($sql)){
				echo $con->error;
				die('There was an error running the query [' . $con->error . ']');
			}
		while($row = $result->fetch_assoc())
		{
			$uid = $row['maxUID'];
			$new_uid = $uid+1;
				
			$sql = 'UPDATE MaxUserID SET maxUID='.$new_uid.' WHERE maxUID='.$uid.';';	
			
			
			if(!$result2 = $con->query($sql)){
				die('There was an error running the query [' . $con->error . ']');
			}
			echo $uid;
		}
	?>
