// index.js
const express = require('express');
const app = express();
const path = require('path');
const bodyParser = require('body-parser');
const axios = require('axios');
const session = require('express-session');

const dummyData = require("./json-samples/blog-post-samples.json");
// const { error } = require('console');

// Middleware to parse JSON request bodies
app.use(express.json());

const cors = require('cors');
app.use(cors());


// Set up sessions
app.use(session({
    secret: 'sogeking123127',

    resave: false,
    saveUninitialized: true,
}));

const PORT = 3000;

app.use(bodyParser.urlencoded({ extended: true }));
// Set EJS as the view engine
app.set('view engine', 'ejs');
// Set the views directory
app.set('views', path.join(__dirname, 'public'));
// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));

// middleware to check if routes are only for authorized user
function isAuthenticated(req, res, next) {
    if (req.session.user) {
        return next(); // User is authenticated, proceed to the next middleware
    } else {
        console.log(`-- user is not authorized --`);
        return res.status(403).redirect('/login'); // User is not authenticated, redirect to login
    }
}

// Routes
app.get('/', async(req, res) => {
    // for home route
    // res.send('<h1>Welcome to iWrite</h1><h2>This is a light-weight microblogging app</h2>');
    res.render('welcome');
});


// Routes
app.get('/hello', async(req, res) => {
    res.render('hello', { message: 'Hello, World!' });

});

app.get('/current-time', async(req, res) => {
    const currentTime = new Date();
    res.json({ message: `Current Time is ${currentTime}` }); // Send JSON response
});

app.get('/login', async(req, res) => {
    const currentTime = new Date();
    console.log(`user shown login page at ${currentTime}`);
    res.render('login', { timestamp : currentTime , error:false});

});


// for home route
app.get('/home', async(req, res) => {
    const currentTime = new Date();
    var isAdmin = false;
    const userData = req.session.user;
    if(userData){
        if(userData.role==='Admin'){
            isAdmin = true;
        }
        console.log(`> authorized user is vewing home ${userData.username}`);
        return res.render('home-new', { timestamp : currentTime , blogPosts:dummyData ,  userAuthorized: true, userData: userData, userId: userData.username , isAdmin:isAdmin});
    }

    console.log(`\t>> unauthorized user shown home page at ${currentTime}`);
    return res.render('home-new', { timestamp : currentTime , blogPosts:dummyData , userAuthorized:false,isAdmin:isAdmin});

});




// // to get blog posts using api
// app.get('/get-blogs-json', async(req, res) => {
//     console.log(`> user requested blogs json`);
//     res.json({ data:dummyData }); // Send JSON response
// });


//  this is going to fetch from spring api and sent to frontend
app.get('/get-blogs-json' ,async(req, res) => {
    console.log(`> user requested blogs json`);
   // res.json({ data:dummyData }); // Send JSON response
    try{

        const urlReq = 'http://localhost:8081/blogs/blog-data-sorted?q=1&page=0&size=40'; // desc
        const apiResponse = await axios.get(urlReq);

        console.log(`>> status code : ${apiResponse.status}`);

        // console.log(JSON.stringify(apiResponse.data));

        res.json({ data:apiResponse.data }); // Send actual JSON response

    }catch(err){
        console.log(`-- failed to get blogs data -- ${err}`);
        res.status(400).json({ message:"request failed -- could not get data from api" }); // Send actual JSON response

    }
});


// // createBlog only shown to valid logged in users
// app.get('/createBlog', async(req, res) => {
//     const currentTime = new Date();
//     console.log(`user shown createBlog page at ${currentTime}`);
//     // userId used as author
//     res.render('create-blog', { timestamp : currentTime , isUserLoggedin:true , userId:"Dr. ABC" });

// });

// post type route which validates request for login from /login page
// Post route to verify credentials
app.post('/verify-credentials', async (req, res) => {
    console.log(`.. user data to verify : `)
    console.log(JSON.stringify(req.body));
    // const { username, password } = req.body; 
    try {
        const response = await axios.post("http://localhost:8081/login",req.body);

        console.log('Response from backend:', JSON.stringify(response.data));
        const isVerified = response.data.valid;
        const userData = response.data.user;
        const role = userData.role;

        console.log(`\n\n >>>>>> type: ${role} has logged in at ${new Date()}`);


        if (isVerified) {
            req.session.user = userData
            if(role==='Admin'){
                console.log(`now secure route is only meant for admin role users`);
                return res.redirect('/admin-route');

            }

            // Redirect to home with user data if non-admin user - - 
            console.log(`non admin user redirected to home with info`);
            // res.render('home-new', { timestamp : new Date() , blogPosts:dummyData , userAuthorized:true , userData:userData});
            return res.redirect('/home');

        } else {
            // redirect back to login with an error
            return res.render('login', { error: 'Invalid credentials. Please try again.' });

        }
    } catch (err) {
        console.log(`.. failed to verify user credentials from the backend server ${err}`);
        // return res.status(400).json({ verified: false, user: null });
        return res.render('login', { error: 'An error occurred while trying to log in. Please try again.' });

    }
});

// dummy secure route
app.get('/admin-route', isAuthenticated, (req, res) => {
    // var user_name = req.session.user.firstName + " " + req.session.user.lastName;
    // res.send(`Welcome, ${user_name}(${req.session.user.username}) This is a secure route.\n You are authorized to view this.Current server time is ${new Date()}`);
    const userData = req.session.user;
    var role = userData.role;

    if(role!=='Admin'){
        console.log(`>>>> secure route is only meant for admin role users <<<<`);
        console.log(`> current user role is ${role}`);

        // return res.redirect('/home');
        return res.send(`Welcome, (${req.session.user.username}) This is a secure route only meant for our administrators.\n <span style="font-size:2rem;">You are not authorized to view this.</span><br> <a style="font-size:2rem;" href="/home"> Click Here to go back </a>`);

    }

    // send to home with user data --
    // no need to send dummy data -- it fetches from frontend --
    // res.render('home-new', { timestamp : new Date() , blogPosts:[] , userAuthorized:true , userData:userData,isAdmin:true});
    res.render('admin-page', { timestamp : new Date() , blogPosts:[] , userAuthorized:true , userData:userData,isAdmin:true});
    /*
        our main idea for this admin route is to have a ADMIN DASHBOARD
        where admin can see all the users in our web app and manage them
        maybe delete a user or create custom role user from here
        for example view we are only showing the home page content here
        but the user role is Admin so it shows up an icon an a message
        for admin level access in future we wish to render a admin dashboard for this
        admin-route url     

    */
});

app.get('/register', async(req, res) => {
    const currentTime = new Date();
    console.log(`user shown register page at ${currentTime}`);
    res.render('register', { timestamp : currentTime});

});

app.post('/api-register-user', async (req, res) => {
    console.log('.. to register user -- \n form data:', req.body);

    try {
        const urlForReq = "http://localhost:8081/createUser";
        const response = await axios.post(urlForReq, req.body, {
            headers: {
                'Content-Type': 'application/json',
            },
        });
        console.log('Response from backend:', JSON.stringify(response.data));

        return res.status(201).send({ register: true, message: response.data });
    } catch (err) {
        console.error('.. failed to verify user credentials from the backend server:', err.message || err);
        
        // Differentiate between various error types
        if (err.response) {
            // The request was made and the server responded with a status code
            // that falls out of the range of 2xx
            res.status(err.response.status || 500).json({ register: false, message: err.response.data || 'Error occurred' });
        } else if (err.request) {
            // The request was made but no response was received
            res.status(500).json({ register: false, message: 'No response from backend server' });
        } else {
            // Something happened in setting up the request that triggered an Error
            res.status(500).json({ register: false, message: 'Error in request setup' });
        }
    }
});


// In your index.js or wherever you handle routes

app.get('/createBlog', (req, res) => {
    if (req.session.user) {
        res.render('create-blog', {
            isUserLoggedin: true,
            username: req.session.user.username,
            userData:req.session.user
        });
    } else {
        res.render('create-blog', {
            isUserLoggedin: false,
            username: null // Pass null or an empty string if not logged in
        });
    }
});



// post create-blog-post to porxy
app.post('/api-create-blog-post', async (req, res) => {
    console.log(`.. to post blog `)
    console.log(JSON.stringify(req.body));
    // const { email, password } = req.body; 
    try {
        const response = await axios.post("http://localhost:8081/blogs/create-blog",req.body);

        // console.log('Response from backend:', JSON.stringify(response.data));
        return res.status(201).send({posted:true,message:response.data});

    } catch (err) {
        console.log(`.. failed to verify user credentials from the backend server ${err}`);
        return res.status(400).json({ posted:false,message:err });
        // return res.render('login', { error: 'An error occurred while trying to log in. Please try again.' });

    }
});



// get type -- get blog by id /blog/<blog-id>
// Implementing the GET /blog/:blogId route

app.get('/blog/:blogId', (req, res) => {
    const userData = req.session.user;
    const blogId = req.params.blogId; // Capture the blog ID from the URL

    console.log(`Blog ID requested: ${blogId}`); // Print the blog ID to the console
    // res.send(`You requested ${blogId}`); // Respond with a message

    if(userData){
        console.log(`> authenticated user is viewing the blog --- ${userData.username}`);
        return res.render('blog-page',{blogId:blogId, userAuthorized:true , userData:userData , userId:userData.username});
    }
    return res.render('blog-page',{blogId:blogId, userAuthorized:false});
  
});

// app.get('/blog/:blogId', async (req, res) => {
//     const blogId = req.params.blogId; // Capture the blog ID from the URL
//     console.log(`Fetching blog with ID: ${blogId}`);

//     try {
//         // Make a request to get the blog data from your API
//         const response = await axios.get(`http://localhost:8081/blogs/get-blog/${blogId}`);
//         const blogData = response.data;

//         // Render the blog page with the retrieved data
//         res.render('blog-page', { blog: blogData });
//     } catch (err) {
//         console.error('Error fetching blog:', err);
//         res.status(500).send('Error fetching blog');
//     }
// });


app.get('/blog', (req, res) => {
    const userData = req.session.user;
    const blogId = req.query.id; // Access blogId from query parameters
    console.log(`> >  > query param  - - Blog ID requested: ${blogId}`);

    if (userData) {
        //  changes made here -- return might be causing issues ---
        // also try return res.render(---------) --- code -- 
        // try both versions
        console.log(`> authenticated user is viewing the blog --- ${userData.username}`);
        res.render('blog-page', { blogId: blogId, userAuthorized: true, userData: userData, userId: userData.username });
    } else {
        console.log(`> unauthenticated user is viewing the blog --- ${''}`);
        res.render('blog-page', { blogId: blogId, userAuthorized: false });
    }
});

// for api blog with id

// api-get-blog
app.get('/api-get-blog/:blogId', async (req, res) => {
    const blogId = req.params.blogId; // Capture the blog ID from the URL
    console.log(`.. to get blog for ${blogId} `);

    // const { email, password } = req.body; 
    try {
        const url = `http://localhost:8081/blogs/get-blog/${blogId}`;
        console.log(`\n.. requesting : ${url}\n`);
        const response = await axios.get(url);

        // console.log('Response from backend:', JSON.stringify(response.data));
        return res.status(201).send({blogFound:true,blog:response.data});

    } catch (err) {
        console.log(`.. failed to verify user credentials from the backend server ${err}`);
        return res.status(400).json({ blogFound:false,blog:null });
        // return res.render('login', { error: 'An error occurred while trying to log in. Please try again.' });

    }
});


// to logout
// POST route for logout
app.post('/logout', (req, res) => {
    
    // Destroy the session and redirect to the home page or login page
    req.session.destroy((err) => {
        if (err) {
            console.error('Error in destroying session: ',err);
            return res.status(500).send('Failed to log out.Please try again');
        }
        res.redirect('/login'); // Redirect to login page or any other page
    });
});

// for user profile
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


app.get('/profile/:userId', async (req, res) => {
    const userId = req.params.userId;
    console.log(`> User profile requested: ${userId}`);
    
    // Assuming user session data is available in req.session.user
    const userDataLoggedIn = req.session.user;

    console.log(`- Fetching user profile JSON from backend -`);
    try {
        const requestedProfile = await getuserInfo(userId); // Fetches from backend
        console.log(' - Requested profile data: - ');
        console.log(requestedProfile.userInfo);
        
        // Check if the user is logged in
        if (userDataLoggedIn) {
            console.log(`>> Logged-in user data: ${JSON.stringify(userDataLoggedIn)}`);
            console.log(`\n> Logged-in user: ${userDataLoggedIn.userId}`);
            console.log(`> Requested profile user: ${userId}\n`);

            const loggedInUserId = String(userDataLoggedIn.userId);
            const requestedUserId = String(userId);

            const isProfileOwner = (loggedInUserId === requestedUserId);
            
            // Check if the logged-in user is viewing their own profile
            // const isProfileOwner = (userDataLoggedIn.userId === userId);
            var message = isProfileOwner ? "viewing their own profile" : "viewing someone else's profile";

            console.log(`>> Authenticated user is ${message} <<`);

            return res.render('user-profile', {
                userId: userId,
                userAuthorized: true,
                userData: userDataLoggedIn,
                userName: userDataLoggedIn.username,
                isProfileOwner: isProfileOwner,
                requestedProfile: requestedProfile.userInfo
            });
        }

        // If the user is not logged in, render the profile as an anonymous user
        console.log(`.. An anonymous user is viewing`);
        return res.render('user-profile', {
            userId: userId,
            userAuthorized: false,
            userData: null,
            userName: null,
            isProfileOwner: false,
            requestedProfile: requestedProfile.userInfo
        });
        
    } catch (err) {
        console.log(`.. Failed to process the request: ${err}`);
        return res.status(500).send('<h1>Welcome to iWrite</h1><h2>This is a light-weight microblogging app</h2><p>Server is not responding, please try again later.</p>');
    }
});


// to update user-profile using backend request : PUT

// PUT request to the Flask API
app.put('/update-profile', async (req, res) => {
    try {

        var user_id = req.body.user_id;

        console.log(`> requested to update user info for ${user_id}`);
        console.log(req.body);
        console.log(`------------------=---------------------=-----------------------------`)
        // Make the proxy request to the Flask API
        const response = await axios.put(`http://localhost:8081/update-user/${user_id}`, req.body, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const resJson = response.data;
        console.log(`>> update response status: ${response.status}`);
        
        // Forward the response back to the client
        res.status(response.status).json(resJson);

    } catch (error) {
        console.log(`> >  errro while updating - - ${error}`)
        // Handle errors here
        if (error.response) {
            console.log(error.response.data);
            console.log(`----------------------------`);
            res.status(error.response.status).json(error.response.data);
        } else {
            res.status(500).json({ message: 'Internal Server Error' });
        }
    }
});

// get count of all users
//  this is going to fetch from spring api and sent to frontend
app.get('/get-user-count' ,async(req, res) => {
    console.log(`> user requested count of users`);
    try{

        const urlReq = 'http://localhost:8081/user-count'; 
        const apiResponse = await axios.get(urlReq);
        const response = await axios.get(urlReq);

        // console.log('Response from backend:', JSON.stringify(response.data));
        console.log(JSON.stringify(apiResponse.data));
        console.log('----------------------------')
        res.status(200).json(apiResponse.data); 
    }catch(err){
        console.log(`-- failed to get users data -- ${err}`);
        res.status(400).json({ message:"request failed -- could not get data from api" }); 

    }
});

// get count of all blogs
//  this is going to fetch from spring api and sent to frontend
app.get('/get-blog-count' ,async(req, res) => {
    console.log(`> user requested count of blogs`);
    try{

        const urlReq = 'http://localhost:8081/blogs/blog-count'; 
        const apiResponse = await axios.get(urlReq);
        console.log(JSON.stringify(apiResponse.data));
        console.log('----------------------------')
        res.status(200).json(apiResponse.data); 

    }catch(err){
        console.log(`-- failed to get blogs data -- ${err}`);
        res.status(400).json({ message:"request failed -- could not get data from api" }); 

    }
});

//  this is going to fetch from spring api and sent to frontend
app.get('/get-users-list' ,async(req, res) => {
    console.log(`> user requested all users list`);
   // res.json({ data:dummyData }); // Send JSON response
    try{
        // later add request params to request more pages - - also need pagination on frontend to work

        const urlReq = 'http://localhost:8081/get-all-users?q=1&page=0&size=100'; 
        const apiResponse = await axios.get(urlReq);
        console.log('----------------------------')
        console.log(apiResponse.data.totalPages);
        console.log(apiResponse.data.pageSize);
        console.log(apiResponse.data.currentPage);
        console.log('----------------------------')
        res.status(200).json(apiResponse.data); // Send actual JSON response

    }catch(err){
        console.log(`-- failed to get blogs data -- ${err}`);
        res.status(400).json({ message:"request failed -- could not get data from api" }); // Send actual JSON response

    }
});

// delete users api proxy

// DELETE request to the Flask API
app.delete('/delete-user/:userId', async (req, res) => {
    const { userId } = req.params; // Extract userId from the route parameter
    console.log(`> admin requested to delete user - - - ${userId}`);

    try {
        const response = await axios.delete(`http://localhost:8081/delete-user/${userId}`);
        
        res.status(response.status).json(response.data);
    } catch (error) {
        console.log(error);
        console.log('---------------------- error while deleting ---------------------------');
        
        if (error.response) {
            res.status(error.response.status).json(error.response.data);
        } else {
            res.status(500).json({ message: 'Internal Server Error' });
        }
    }
});

// Start the server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});