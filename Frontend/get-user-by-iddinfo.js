// api-get-user-by-id
async function getuserInfo(userId){
    // helper function to make request

    var req_body = {'userId':userId};
    console.log(`.. to get user-by-id for ${userId} `);
    console.log(JSON.stringify(req_body));

    try {
        const url = `http://localhost:8081/get-user-by-id`;
        const response = await axios.post(url,req_body);

        // console.log('Response from backend:', JSON.stringify(response.data));
        return {userFound:true,userInfo:response.data};

    } catch (err) {
        console.log(`.. failed to verify user credentials from the backend server ${err}`);
        return {userFound:false,userInfo:null};

    }
}