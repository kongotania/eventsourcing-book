"use client"
import {useState} from 'react'
import Head from 'next/head'
import {v4} from "uuid";
import {addItem} from "@/app/components/additem/additem";
import {clearcart} from "@/app/components/clearcart/clearcart";
import {CartItems} from "@/app/components/cartitems/cartitems";
import {submitcart} from "@/app/components/submitcart/submitcart";
import {changeInventory} from "@/app/components/changeinventory/changeinventory";
import {changeprice} from "@/app/components/changeprice/changeprice";


export default function Home() {
    const [quantity, setQuantity] = useState(100)
    const [price, setPrice] = useState<number>()

    const [cartItemVersion, setCartItemVersion] = useState(-1)

    const cartId = "691538cf-7925-4693-9926-1727a918858c"
    const productId = "fb9451c8-5fe2-4295-98be-9f7d90b3e7f2"

    return (
        <div className="container">
            <Head>
                <title>Coffee Order</title>
                <link rel="icon" href="/favicon.ico"/>
            </Head>

            <main className="section content">
                <h1 className={"book-font"}>Understanding Eventsourcing</h1>
                <div className="box">
                    <CartItems expectedVersion={cartItemVersion} cartId={cartId}/>

                    <hr/>

                    <div className="columns is-vcentered">
                        <div className="column has-text-right">
                            <button
                                className="button is-link mr-3"
                                onClick={() => clearcart(cartId).then(response => setCartItemVersion(response.aggregateSequence??-1))}
                            >
                                Clear
                            </button>
                            <button className="button is-primary" onClick={async () => {
                                await submitcart(cartId).then(response => setCartItemVersion(response.aggregateSequence??-1))
                            }}>
                                Order now
                            </button>
                        </div>
                    </div>
                </div>
                <div className={"container columns"}>
                    <div className={"column is-one-third"}>
                        <div className={"button is-info  top-margin"}
                             onClick={() => addItem(cartId, productId).then(response => setCartItemVersion(response.aggregateSequence??-1))}>Add
                            Item
                        </div>
                    </div>

                    <div className={"column is-one-third"}>
                        <input onChange={(evt) => setQuantity(Number(evt.target.value))} type={"text"}
                               className={"input"}/>
                        <span className={"button is-info  top-margin"} onClick={async () => {
                            await changeInventory(productId, quantity)
                        }}>Adjust Inventory
                        </span></div>
                    <div className={"column is-one-third "}>
                        <input onChange={(evt) => setPrice(Number(evt.target.value))} type={"text"}
                               className={"input"}/>
                        <span className={"button is-info top-margin"} onClick={async () => {
                            await changeprice(productId, price!!, 10.99)
                        }}>Adjust Price
                        </span></div>
                </div>
            </main>
        </div>
    )
}

