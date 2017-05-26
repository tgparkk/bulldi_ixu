<?php 
 
require_once '../includes/DbOperations.php';
 
$response = array(); 
 
if($_SERVER['REQUEST_METHOD']=='POST') {
    if(
        isset($_POST['temp']) and 
                isset($_POST['co'])) 
        {
        //operate the data further
        $db = new DbOperations();

        $result = $db->test(   $_POST['temp'],
                            $_POST['co']
                                );

        if($result == 1){
            $response['error'] = false; 
            $response['message'] = "User registered successfully";
        } elseif($result == 2){
            $response['error'] = true; 
            $response['message'] = "Some error occurred please try again";  
        }
 
    } else{
        $response['error'] = true; 
        $response['message'] = "Required fields are missing";
    }
} else{
    $response['error'] = true; 
    $response['message'] = "Invalid Request";
}
 
echo json_encode($response);