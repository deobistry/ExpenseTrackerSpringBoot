import { useEffect, useState } from "react";
import axios from "axios";
import "./index.css";


const api = axios.create({
    baseURL: "http://localhost:7878"
});


api.interceptors.request.use((config) => {

    const token = localStorage.getItem("token");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;

});



function App() {


    const [token, setToken] = useState(
        localStorage.getItem("token")
    );


    const [activePage, setActivePage] =
        useState("dashboard");


    const [authMode, setAuthMode] =
        useState("login");



    const [name, setName] =
        useState("");

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");



    const [categories, setCategories] =
        useState([]);


    const [categoryTitle, setCategoryTitle] =
        useState("");



    const [expenses, setExpenses] =
        useState([]);



    const [description, setDescription] =
        useState("");

    const [amount, setAmount] =
        useState("");

    const [date, setDate] =
        useState("");

    const [categoryId, setCategoryId] =
        useState("");



    const [message, setMessage] =
        useState("");



    useEffect(() => {

        if (token) {

            loadCategories();

            loadExpenses();

        }

    }, [token]);




    async function signup() {

        try {

            await api.post(
                "/auth/signup",
                {
                    name,
                    email,
                    password
                }
            );


            setMessage(
                "Account created. Please login."
            );


            setAuthMode("login");


        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Signup failed"
            );

        }

    }





    async function login() {

        try {


            const response =
                await api.post(
                    "/auth/login",
                    {
                        email,
                        password
                    }
                );


            localStorage.setItem(
                "token",
                response.data.token
            );


            setToken(
                response.data.token
            );


            setMessage("");

            setActivePage("dashboard");


        } catch (error) {


            setMessage(
                "Invalid email or password"
            );


        }

    }





    function logout() {

        localStorage.removeItem(
            "token"
        );


        setToken(null);


        setEmail("");

        setPassword("");


        setAuthMode("login");

    }





    async function loadCategories() {

        try {


            const response =
                await api.get(
                    "/categories"
                );


            setCategories(
                response.data
            );


        } catch (error) {

            console.log(error);

        }

    }





    async function createCategory() {


        if (!categoryTitle.trim()) {

            return;

        }


        try {


            await api.post(
                "/categories",
                {
                    title: categoryTitle
                }
            );


            setCategoryTitle("");


            loadCategories();


        } catch (error) {


            setMessage(
                "Category creation failed"
            );


        }

    }





    async function deleteCategory(id) {


        try {


            await api.delete(
                `/categories/${id}`
            );


            loadCategories();


        } catch (error) {


            setMessage(
                error.response?.data?.message ||
                "Cannot delete category"
            );


        }

    }





    async function loadExpenses() {


        try {


            const response =
                await api.get(
                    "/expenses"
                );


            setExpenses(
                response.data
            );


        } catch (error) {


            console.log(error);


        }

    }





    async function createExpense() {


        try {


            await api.post(
                "/expenses",
                {
                    description,
                    amount,
                    date,
                    categoryId
                }
            );


            setDescription("");

            setAmount("");

            setDate("");

            setCategoryId("");


            loadExpenses();


        } catch (error) {


            setMessage(
                "Expense creation failed"
            );


        }

    }

        async function deleteExpense(id) {


        try {


            await api.put(
                `/expenses/delete/${id}`
            );


            loadExpenses();


        } catch (error) {


            setMessage(
                "Unable to delete expense"
            );


        }

    }





    function totalExpenseAmount() {

        return expenses
            .reduce(
                (sum, expense) =>
                    sum + Number(expense.amount || 0),
                0
            )
            .toFixed(2);

    }





    function renderAuth() {


        return (

            <div className="auth-page">


                <div className="auth-card">


                    <div className="brand">

                        <h1>
                            Expense Manager
                        </h1>

                        <p>
                            Track your money smarter
                        </p>

                    </div>



                    {
                        message &&

                        <div className="alert">

                            {message}

                        </div>

                    }



                    {
                    authMode === "login" ?

                    <>

                        <h2>
                            Welcome Back
                        </h2>


                        <input
                            type="email"
                            placeholder="Email address"
                            value={email}
                            onChange={
                                e =>
                                setEmail(
                                    e.target.value
                                )
                            }
                        />


                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={
                                e =>
                                setPassword(
                                    e.target.value
                                )
                            }
                        />



                        <button
                            className="primary-btn"
                            onClick={login}
                        >
                            Login
                        </button>



                        <p className="switch-text">

                            Don't have an account?


                            <button
                                className="text-btn"
                                onClick={
                                    () =>
                                    setAuthMode("signup")
                                }
                            >
                                Sign up
                            </button>


                        </p>


                    </>


                    :


                    <>

                        <h2>
                            Create Account
                        </h2>



                        <input
                            placeholder="Full name"
                            value={name}
                            onChange={
                                e =>
                                setName(
                                    e.target.value
                                )
                            }
                        />



                        <input
                            type="email"
                            placeholder="Email address"
                            value={email}
                            onChange={
                                e =>
                                setEmail(
                                    e.target.value
                                )
                            }
                        />



                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={
                                e =>
                                setPassword(
                                    e.target.value
                                )
                            }
                        />



                        <button
                            className="primary-btn"
                            onClick={signup}
                        >
                            Create Account
                        </button>




                        <p className="switch-text">

                            Already registered?


                            <button
                                className="text-btn"
                                onClick={
                                    () =>
                                    setAuthMode("login")
                                }
                            >
                                Login
                            </button>


                        </p>


                    </>

                    }


                </div>


            </div>

        );


    }







    function renderDashboard() {


        return (

            <>


                <h1>
                    Dashboard
                </h1>



                <div className="stats">


                    <div className="stat-card">

                        <span>
                            Categories
                        </span>


                        <strong>
                            {categories.length}
                        </strong>

                    </div>





                    <div className="stat-card">

                        <span>
                            Expenses
                        </span>


                        <strong>
                            {expenses.length}
                        </strong>

                    </div>





                    <div className="stat-card">

                        <span>
                            Total Spending
                        </span>


                        <strong>
                            ${totalExpenseAmount()}
                        </strong>

                    </div>



                </div>





                <div className="welcome-card">

                    <h2>
                        Welcome back 👋
                    </h2>


                    <p>
                        Manage your expenses,
                        categories and spending
                        from one place.
                    </p>


                </div>


            </>

        );

    }







    function renderCategories() {


        return (

            <div className="content-card">


                <h1>
                    Categories
                </h1>



                <div className="form-row">


                    <input

                        placeholder="Enter category name"

                        value={categoryTitle}

                        onChange={
                            e =>
                            setCategoryTitle(
                                e.target.value
                            )
                        }

                    />


                    <button
                        className="primary-btn"
                        onClick={createCategory}
                    >
                        Add
                    </button>


                </div>





                <div className="category-grid">


                {
                    categories.length === 0 ?

                    <p>
                        No categories created yet.
                    </p>


                    :


                    categories.map(
                        category => (

                        <div
                            className="category-card"
                            key={category.id}
                        >


                            <div>

                                <span className="icon">
                                    📁
                                </span>


                                <strong>
                                    {category.title}
                                </strong>

                            </div>




                            <button

                                className="delete-btn"

                                onClick={
                                    () =>
                                    deleteCategory(
                                        category.id
                                    )
                                }

                            >
                                Delete
                            </button>



                        </div>

                    ))

                }


                </div>


            </div>

        );

    }

        function renderExpenses() {


        return (

            <div className="content-card">


                <h1>
                    Expenses
                </h1>




                <div className="expense-form">


                    <input
                        placeholder="Description"
                        value={description}
                        onChange={
                            e =>
                            setDescription(
                                e.target.value
                            )
                        }
                    />



                    <input
                        type="number"
                        placeholder="Amount"
                        value={amount}
                        onChange={
                            e =>
                            setAmount(
                                e.target.value
                            )
                        }
                    />



                    <input
                        type="date"
                        value={date}
                        onChange={
                            e =>
                            setDate(
                                e.target.value
                            )
                        }
                    />



                    <select

                        value={categoryId}

                        onChange={
                            e =>
                            setCategoryId(
                                e.target.value
                            )
                        }

                    >


                        <option value="">
                            Select Category
                        </option>


                        {
                            categories.map(
                                category => (

                                <option

                                    key={category.id}

                                    value={category.id}

                                >

                                    {category.title}

                                </option>

                            ))
                        }


                    </select>



                    <button

                        className="primary-btn"

                        onClick={createExpense}

                    >

                        Add Expense

                    </button>



                </div>






                <div className="table-wrapper">


                    <table>


                        <thead>

                            <tr>

                                <th>
                                    Description
                                </th>


                                <th>
                                    Amount
                                </th>


                                <th>
                                    Date
                                </th>


                                <th>
                                    Category
                                </th>


                                <th>
                                    Action
                                </th>


                            </tr>


                        </thead>





                        <tbody>


                        {
                            expenses.length === 0 ?


                            <tr>

                                <td colSpan="5">

                                    No expenses found.

                                </td>

                            </tr>



                            :



                            expenses.map(
                                expense => (

                                <tr
                                    key={
                                        expense.expenseId
                                    }
                                >


                                    <td>

                                        {
                                            expense.description
                                        }

                                    </td>



                                    <td>

                                        $
                                        {
                                            expense.amount
                                        }

                                    </td>



                                    <td>

                                        {
                                            expense.date
                                        }

                                    </td>



                                    <td>

                                        {
                                            expense.categoryTitle
                                        }

                                    </td>




                                    <td>


                                        <button

                                            className="delete-btn"

                                            onClick={
                                                () =>
                                                deleteExpense(
                                                    expense.expenseId
                                                )
                                            }

                                        >

                                            Delete

                                        </button>


                                    </td>



                                </tr>

                            ))

                        }


                        </tbody>


                    </table>


                </div>



            </div>

        );


    }









    if (!token) {

        return renderAuth();

    }







    return (


        <div className="app-container">


            <nav className="navbar">


                <div className="logo">

                    Expense Manager

                </div>





                <div className="nav-links">


                    <button

                        className={
                            activePage === "dashboard"
                            ? "active"
                            : ""
                        }

                        onClick={
                            () =>
                            setActivePage(
                                "dashboard"
                            )
                        }

                    >

                        🏠 Dashboard

                    </button>





                    <button

                        className={
                            activePage === "categories"
                            ? "active"
                            : ""
                        }

                        onClick={
                            () =>
                            setActivePage(
                                "categories"
                            )
                        }

                    >

                        📁 Categories

                    </button>





                    <button

                        className={
                            activePage === "expenses"
                            ? "active"
                            : ""
                        }

                        onClick={
                            () =>
                            setActivePage(
                                "expenses"
                            )
                        }

                    >

                        💰 Expenses

                    </button>




                    <button

                        className="logout-btn"

                        onClick={logout}

                    >

                        Logout

                    </button>



                </div>



            </nav>







            <main className="page-container">


                {
                    activePage === "dashboard" &&
                    renderDashboard()
                }



                {
                    activePage === "categories" &&
                    renderCategories()
                }



                {
                    activePage === "expenses" &&
                    renderExpenses()
                }



            </main>



        </div>


    );


}


export default App;