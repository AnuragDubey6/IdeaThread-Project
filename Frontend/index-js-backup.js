// index.js
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const path = require('path');
const axios = require('axios'); // for 3rd party requests
const connection  = require('./db'); // MySQL Connection


app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

const dummyData = require("./json-samples/blog-post-samples.json");


const PORT = 3000;


// Set EJS as the view engine - for templates
app.set('view engine', 'ejs');
// Set the views directory
app.set('views', path.join(__dirname, 'public'));
// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public'))); 


// Routes
app.get('/', async(req, res) => {
    console.log(`user has requested base route at ${new Date()}`);
    // for home route
    // res.send('<h1>Welcome to iWrite</h1><h2>This is a light-weight microblogging app</h2>');
    res.render('welcome');
});

app.get('/current-time', async(req, res) => {
    // this sends a json response to frontend 
    const currentTime = new Date();
    res.json({ message: `Current Time is ${currentTime}` }); // Send JSON response
});

// Routes
app.get('/hello', async(req, res) => {
    res.render('hello', { message: 'Hello, World!' });

});


app.get('/register', async(req, res) => {
    const currentTime = new Date();
    console.log(`user shown register page at ${currentTime}`);
    res.render('register', { timestamp : currentTime});

});


app.get('/login', async(req, res) => {
    const currentTime = new Date();
    console.log(`user shown login page at ${currentTime}`);
    res.render('login', { timestamp : currentTime});

});



// for home route with dummy data
app.get('/home', async(req, res) => {
    const currentTime = new Date();
    console.log(`user shown home page at ${currentTime}`);
    res.render('home', { timestamp : currentTime , blogPosts:dummyData });

});

// //to get blog posts using api -- this is to send dummy data
// app.get('/get-blogs-json', async(req, res) => {
//     console.log(`> user requested blogs json`);
//     res.json({ data:dummyData }); // Send JSON response
// });

//  this is going to fetch from spring api and sent to frontend
app.get('/get-blogs-json' ,async(req, res) => {
    console.log(`> user requested blogs json`);
   // res.json({ data:dummyData }); // Send JSON response
    try{
        // from localhost 80

        // let config = {
        //     method: 'get',
        //     maxBodyLength: Infinity,
        //     url: 'http://localhost:8081/blogs/blog-data-sorted?q=1',
        //     headers: { 
        //       'Content-Type': 'application/json'
        //     },

        const urlReq = 'http://localhost:8081/blogs/blog-data-sorted?q=0&page=0&size=5'; // desc
        const apiResponse = await axios.get(urlReq);

        console.log(`>> status code : ${apiResponse.status}`);

        console.log(JSON.stringify(apiResponse.data));

        res.json({ data:apiResponse.data }); // Send actual JSON response

    }catch(err){
        console.log(`-- failed to get blogs data -- ${err}`);
        res.status(400).json({ message:"request failed -- could not get data from api" }); // Send actual JSON response

    }
});

app.post('/verify-credentials', async (req, res) => {
    console.log(`.. user data to verify : `)
    console.log(JSON.stringify(req.body));
    // const { email, password } = req.body; 
    try {

        const loginApi = `"http://localhost:8081/login"`; // url for backend api login route
        const response = await axios.post(loginApi,req.body);

        console.log('Response from backend:', JSON.stringify(response.data));
        const isVerified = response.data.verified;
        const userData = response.data.user;

        if (isVerified) {
            req.session.user = response.data.user;
            // Redirect to a secured route after successful login
            return res.redirect('/secure-route');
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


// listen to requests at 3000
// Start the server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});