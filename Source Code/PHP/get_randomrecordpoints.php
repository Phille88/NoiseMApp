<?php
 
/*
 * Following code will create a new profile row
 * All profile details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for required fields
if (isset($_POST['userID']) && isset($_POST['latitude']) && isset($_POST['longitude']) && isset($_POST['dB']) && isset($_POST['accuracy']) && isset($_POST['quality']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
 
    $userID = mysql_real_escape_string($_POST['userID']);
    $latitude = mysql_real_escape_string($_POST['latitude']);
	$longitude = mysql_real_escape_string($_POST['longitude']);
	$dB = mysql_real_escape_string($_POST['dB']);
	$accuracy = mysql_real_escape_string($_POST['accuracy']);
	$quality = mysql_real_escape_string($_POST['quality']);
	
	//DO A RECORDING
	$response["points"] = array();
	$response["badges"] = array();
	
	$points["amount"] = 2;
	$points["description"] = "You did a recording.";
	array_push($response["points"], $points);
	$badgeID = 0;
    // mysql inserting a new row
	//FIRST IN RADIUS
	$all_my_recordings_result = mysql_query("SELECT nr.* FROM noiserecordings nr WHERE nr.noiseID NOT IN (SELECT sbr.noiseRecordingID FROM soundbattlerecordings sbr WHERE sbr.userID = $userID) AND nr.noiseID NOT IN (SELECT nhr.noiseRecordingID FROM noisehuntrecordings nhr WHERE nhr.userID = $userID) AND userID = $userID");// AND (latitude != $latitude OR longitude != $longitude OR dB != $dB OR accuracy != $accuracy OR quality != $quality) ORDER BY nr.timeStamp");
	
	if(mysql_num_rows($all_my_recordings_result) == 500){
		$points["amount"] = 100;
		$points["description"] = "This is your 500th recording!";
		array_push($response["points"], $points);
		
		$badge["badgeID"] = 0500;
		$badge["amount"] = 50;
		$badge["description"] = "You earned a badge!";
		$badgeID = $badge["badgeID"];
		array_push($response["badges"], $badge);
	}
	elseif(mysql_num_rows($all_my_recordings_result) == 250){
		$points["amount"] = 100;
		$points["description"] = "This is your 250th recording!";
		array_push($response["points"], $points);
	}
	elseif(mysql_num_rows($all_my_recordings_result) == 100){
		$points["amount"] = 100;
		$points["description"] = "This is your 100th recording!";
		array_push($response["points"], $points);
		
		$badge["badgeID"] = 0100;
		$badge["amount"] = 50;
		$badge["description"] = "You earned a badge!";
		$badgeID = $badge["badgeID"];
		array_push($response["badges"], $badge);
	}
	elseif(mysql_num_rows($all_my_recordings_result) == 50){
		$points["amount"] = 75;
		$points["description"] = "This is your 50th recording!";
		array_push($response["points"], $points);
		
		$badge["badgeID"] = 0050;
		$badge["amount"] = 50;
		$badge["description"] = "You earned a badge!";
		$badgeID = $badge["badgeID"];
		array_push($response["badges"], $badge);
	}
	elseif(mysql_num_rows($all_my_recordings_result) == 20){
		$points["amount"] = 50;
		$points["description"] = "This is your 20th recording!";
		array_push($response["points"], $points);
	}
	elseif(mysql_num_rows($all_my_recordings_result) == 10){
		$points["amount"] = 20;
		$points["description"] = "This is your 10th recording!";
		array_push($response["points"], $points);
	}
	elseif(mysql_num_rows($all_my_recordings_result) == 5){
		$points["amount"] = 10;
		$points["description"] = "This is your 5th recording!";
		array_push($response["points"], $points);
	}
	elseif(mysql_num_rows($all_my_recordings_result) == 1){
		$points["amount"] = 25;
		$points["description"] = "This is your first recording!";
		array_push($response["points"], $points);
		
		$badge["badgeID"] = 0001;
		$badge["amount"] = 50;
		$badge["description"] = "You earned the Newbie badge!";
		$badgeID = $badge["badgeID"];
		array_push($response["badges"], $badge);
	}
	
	
	if (mysql_num_rows($all_my_recordings_result) > 0) {
		$min_distance =  INF;
		while ($row = mysql_fetch_array($all_my_recordings_result)) {
			$distance = DistanceTo($latitude,$longitude,$row["latitude"],$row["longitude"]) ;
			if($distance < $min_distance){
				$min_distance = $distance;
			}
		}
		if ($min_distance >= 3000){
			$points["amount"] = 70;
			$points["description"] = "First recording in a radius of 3 km!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 2000){
			$points["amount"] = 35;
			$points["description"] = "First recording in a radius of 2 km!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 1000){
			$points["amount"] = 20;
			$points["description"] = "First recording in a radius of 1 km!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 500){
			$points["amount"] = 10;
			$points["description"] = "First recording in a radius of 500 m!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 200){
			$points["amount"] = 5;
			$points["description"] = "First recording in a radius of 200 m!";
			array_push($response["points"], $points);
		}
	}
	
	$all_friends_recordings_result = mysql_query("SELECT nr.* FROM noiserecordings nr JOIN friendships fs ON fs.userID2 = nr.userID WHERE userID1 = $userID AND nr.noiseID NOT IN (SELECT sbr.noiseRecordingID FROM soundbattlerecordings sbr)");
	
	if (mysql_num_rows($all_friends_recordings_result) > 0) {
		$min_distance =  INF;
		while ($row = mysql_fetch_array($all_friends_recordings_result)) {
			$distance = DistanceTo($latitude,$longitude,$row["latitude"],$row["longitude"]) ;
			if($distance < $min_distance){
				$min_distance = $distance;
			}
		}
		if ($min_distance >= 3000){
			$points["amount"] = 140;
			$points["description"] = "First of your friends to record here in a radius of 3 km!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 2000){
			$points["amount"] = 70;
			$points["description"] = "First of your friends to record here in a radius of 2 km!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 1000){
			$points["amount"] = 40;
			$points["description"] = "First of your friends to record here in a radius of 1 km!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 500){
			$points["amount"] = 20;
			$points["description"] = "First of your friends to record here in a radius of 500 m!";
			array_push($response["points"], $points);
		}
		elseif($min_distance >= 200){
			$points["amount"] = 10;
			$points["description"] = "First of your friends to record here in a radius of 200 m!";
			array_push($response["points"], $points);
		}
	}
	
	if($quality > 0.75){
		$points["amount"] = 50;
		$points["description"] = "Studio quality!";
		array_push($response["points"], $points);
	}
	elseif($quality > 0.50){
		$points["amount"] = 20;
		$points["description"] = "Quality is ok!";
		array_push($response["points"], $points);
	}
    
	$totalPoints = 0;
	foreach($response["points"] as $point){
		$totalPoints = $totalPoints + $point["amount"];
	}
	unset($point);
	
	if(!empty($response["badges"])){
		foreach($response["badges"] as $badge){
			$totalPoints = $totalPoints + $badge["amount"];
		}
		unset($badge);
	}
	
	$result = mysql_query("INSERT INTO totalpoints (userID, totalPoints) VALUES ($userID, $totalPoints) ON DUPLICATE KEY UPDATE totalPoints = totalPoints + $totalPoints");
	if($badgeID != 0){
		$update_badges = mysql_query("INSERT INTO badges (userID, badgeID) VALUES ($userID, $badgeID)");
	}
	
	if($result) {
		$response["success"] = 1;
		$response["message"] = $totalPoints . " points are succesfully stored.";
		$response["totalPoints"] =  $totalPoints;
	}
	else{
		$response["success"] = 0;
		$response["message"] = "Something went wrong :s, totalpoints: " . $totalPoints . " for userID " . $userID;
	}
 
	// echoing JSON response
	echo json_encode($response);

} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}



function DistanceTo($lat1, $lon1, $lat2, $lon2){  
    /* $lat1,$lon1 center of circle */
    /* $lat2,$lon2 Point coordinates */
    $R = 6371; // km
    $dLat = deg2rad($lat2-$lat1);
    $dLon = deg2rad($lon2-$lon1);
    $lat1 = deg2rad($lat1);
    $lat2 = deg2rad($lat2);

    $a = sin($dLat/2)*sin($dLat/2)+sin($dLon/2)*sin($dLon/2) * cos($lat1) * cos($lat2); 
    $c = 2 * atan2(sqrt($a), sqrt(1-$a)); 
    $d = $R * $c;
    $d = round(($d*1000),2);
    
	return $d;
}
?>