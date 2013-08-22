<?php
 
/*
 * Following code will create a new profile row
 * All profile details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for required fields
if (isset($_POST['userID']) && isset($_POST['soundBattleID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    $userID = mysql_real_escape_string($_POST['userID']);
	$soundBattleID = mysql_real_escape_string($_POST['soundBattleID']);
	
	
	// Set timezone
	date_default_timezone_set("UTC");
	
	$response["points"] = array();
	$response["badges"] = array();
	$response["opp_points"] = array();
    
	$all_my_soundbattle_recordings_result = mysql_query("SELECT nr.*, sbl.latitude AS SBLlatitude, sbl.longitude AS SBLlongitude FROM noiserecordings nr 
JOIN soundbattlerecordings sbr ON nr.noiseID = sbr.noiseRecordingID
JOIN soundbattlelocations sbl ON sbr.soundBattleLocationID = sbl.soundBattleLocationID
WHERE sbl.soundBattleID = $soundBattleID AND nr.userID = $userID");

	$all_opponent_soundbattle_recordings_result = mysql_query("SELECT nr.*, sbl.latitude AS SBLlatitude, sbl.longitude AS SBLlongitude FROM noiserecordings nr 
JOIN soundbattlerecordings sbr ON nr.noiseID = sbr.noiseRecordingID
JOIN soundbattlelocations sbl ON sbr.soundBattleLocationID = sbl.soundBattleLocationID
WHERE sbl.soundBattleID = $soundBattleID AND nr.userID != $userID");
	
	if((mysql_num_rows($all_my_soundbattle_recordings_result) + mysql_num_rows($all_opponent_soundbattle_recordings_result)) == 6){
		
		$my_qualities = array();
		$my_accuracies = array();
		$my_timestamps = array();
		while ($row = mysql_fetch_array($all_my_soundbattle_recordings_result)) {
			array_push($my_qualities,$row["quality"]);
			array_push($my_accuracies,DistanceTo($row["latitude"], $row["longitude"], $row["SBLlatitude"], $row["SBLlongitude"]));
			array_push($my_timestamps,$row["timeStamp"]);
		}
		
		$my_points["quality"] = calculateQualityPoints($my_qualities);
		$my_points["accuracy"] = calculateAccuracyPoints($my_accuracies);
		$my_points["speed"] = calculateSpeedPoints($my_timestamps);
		$my_points["totalPoints"] = $my_points["quality"] + $my_points["accuracy"] + $my_points["speed"];
		
		array_push($response["points"], $my_points);
		
		
		$opp_qualities = array();
		$opp_accuracies = array();
		$opp_timestamps = array();
		while ($row = mysql_fetch_array($all_opponent_soundbattle_recordings_result)) {
			array_push($opp_qualities,$row["quality"]);
			array_push($opp_accuracies,DistanceTo($row["latitude"], $row["longitude"], $row["SBLlatitude"], $row["SBLlongitude"]));
			array_push($opp_timestamps,$row["timeStamp"]);
		}
		
		$opp_points["quality"] = calculateQualityPoints($opp_qualities);
		$opp_points["accuracy"] = calculateAccuracyPoints($opp_accuracies);
		$opp_points["speed"] = calculateSpeedPoints($opp_timestamps);
		$opp_points["totalPoints"] = $opp_points["quality"] + $opp_points["accuracy"] + $opp_points["speed"];
		
		array_push($response["opp_points"], $opp_points);
		
		$response["success"] = 1;
		$response["message"] = "Sound Battle " . $soundBattleID . " points acquired.";
		
		
		$my_totalPoints = $my_points["totalPoints"];
		$opp_totalPoints = $opp_points["totalPoints"];
		
		$result = mysql_query("SELECT sb.* FROM soundbattles sb WHERE sb.soundBattleID = $soundBattleID");
		if(!empty($result)){
			$result = mysql_fetch_array($result);
			if($userID == $result["userID1"]){
				$insertion = mysql_query("INSERT into soundbattletotalpoints (soundBattleID, pointsUserID1, pointsUserID2) VALUES ($soundBattleID, $my_totalPoints, $opp_totalPoints) ");
			}
			else{
				$insertion = mysql_query("INSERT into soundbattletotalpoints (soundBattleID, pointsUserID1, pointsUserID2) VALUES ($soundBattleID, $opp_totalPoints, $my_totalPoints) ");
			}
		}
		
		$soundBattlesWon = mysql_query("SELECT soundBattleID FROM (SELECT sbtp.soundBattleID, sbtp.pointsUserID1 AS myPoints, sbtp.pointsUserID2 AS oppPoints, sbtp.createdAt FROM soundbattletotalpoints sbtp JOIN soundbattles sb ON sb.soundBattleID = sbtp.soundBattleID WHERE sb.userID1 = $userID UNION SELECT sbtp.soundBattleID, sbtp.pointsUserID2 AS myPoints, sbtp.pointsUserID1 AS oppPoints, sbtp.createdAt FROM soundbattletotalpoints sbtp JOIN soundbattles sb ON sb.soundBattleID = sbtp.soundBattleID WHERE sb.userID2 = $userID) AS sbpoints WHERE sbpoints.myPoints > sbpoints.oppPoints ORDER BY sbpoints.createdAt");
		if(!empty($soundBattlesWon)){
			$soundBattlesWonOrdered = array();
			while($row = mysql_fetch_array($soundBattlesWon)){
				array_push($soundBattlesWonOrdered, $row["soundBattleID"]);
			}
			$badgeID = 0;
			if(sizeof($soundBattlesWonOrdered) >= 1 && $soundBattlesWonOrdered[0] == $soundBattleID){
				$badge["badgeID"] = 1001;
				$badge["description"] = "You have won your first Sound Battle!";
				$badge["amount"] = 50;
				$badgeID = $badge["badgeID"];
				array_push($response["badges"], $badge);
			}
			elseif(sizeof($soundBattlesWonOrdered) >= 5 && $soundBattlesWonOrdered[4] == $soundBattleID){
				$badge["badgeID"] = 1005;
				$badge["description"] = "You have won your fifth Sound Battle!";
				$badge["amount"] = 50;
				$badgeID = $badge["badgeID"];
				array_push($response["badges"], $badge);
			}
			elseif(sizeof($soundBattlesWonOrdered) >= 20 && $soundBattlesWonOrdered[19] == $soundBattleID){
				$badge["badgeID"] = 1020;
				$badge["description"] = "You have won your twentieth Sound Battle!";
				$badge["amount"] = 50;
				$badgeID = $badge["badgeID"];
				array_push($response["badges"], $badge);
			}
			elseif(sizeof($soundBattlesWonOrdered) >= 50 && $soundBattlesWonOrdered[49] == $soundBattleID){
				$badge["badgeID"] = 1050;
				$badge["description"] = "You have won your fifthieth Sound Battle!";
				$badge["amount"] = 50;
				$badgeID = $badge["badgeID"];
				array_push($response["badges"], $badge);
			}
			elseif(sizeof($soundBattlesWonOrdered) >= 100 && $soundBattlesWonOrdered[99] == $soundBattleID){
				$badge["badgeID"] = 1100;
				$badge["description"] = "You have won your hundrieth Sound Battle!";
				$badge["amount"] = 50;
				$badgeID = $badge["badgeID"];
				array_push($response["badges"], $badge);
			}
			if($badgeID != 0){
				$update_badges = mysql_query("INSERT INTO badges (userID, badgeID) VALUES ($userID, $badgeID)");
				$badgePoints = $badge["amount"];
				$result = mysql_query("INSERT INTO totalpoints (userID, totalPoints) VALUES ($userID, $badgePoints) ON DUPLICATE KEY UPDATE totalPoints = totalPoints + $badgePoints");
			}
		}
	}
	else {
		$response["success"] = 0;
		$response["message"] = "Sound Battle " . $soundBattleID . " is not finished yet!";
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
 
function timeInterval($time1, $time2, $time3) {
    $times[0] = strtotime($time1);
	$times[1] = strtotime($time2);
	$times[2] = strtotime($time3);
	
    $diffs = array();
    
	for ($i = 0; $i < 3; $i++) {
		$diffs[$i] = abs($times[$i%3] - $times[($i+1)%3]);
	}
	
	$max_diff = 0;
	foreach($diffs as $diff){
		if($diff > $max_diff){
			$max_diff = $diff;
		}
	}
	return $max_diff;
}

function calculateSpeedPoints($time_array){
	if(sizeof($time_array)==3){
		$time = timeInterval($time_array[0],$time_array[1],$time_array[2]);
	}
	return round(1/pow($time,0.5) * 6000);
}

function calculateAccuracyPoints($accuracy_array){
	$points = 0;
	if(sizeof($accuracy_array)==3){
		foreach($accuracy_array as $accuracy){
			if($accuracy == 0) {
				$points += 400;
			}
			else {
				$points += round(300 - 20 * $accuracy);
				if($points < 0)
					$points = 0;
			}
		}
	}
	return $points;
}

function calculateQualityPoints($quality_array){
	$points = 0;
	if(sizeof($quality_array)==3){
		foreach($quality_array as $quality){
			$points += $quality * 200;
		}
	}
	return $points;
}
?>