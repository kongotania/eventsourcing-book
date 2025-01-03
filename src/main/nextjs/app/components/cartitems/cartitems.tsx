import {useEffect, useState} from "react";
import {removeItem} from "@/app/components/removeItem/removeItem";
import {Inventories} from "@/app/components/inventories/inventories";
import {Bars, Blocks} from "react-loader-spinner";

export const CartItems = (props: { cartId: string, expectedVersion: number }) => {

    const [cartItems, setCartItems] = useState<{ totalPrice: number, data: any[] }>({totalPrice: 0, data: []})
    const [loading, setLoading] = useState(false)
    const [expectedVersion, setExpectedVersion] = useState(-1)

    const fetchCartItems = async (expectedVersion) => {
        setLoading(true)
        let response = await fetch(`/cartitems/${props.cartId}?expectedVersion=${expectedVersion??-1}`)
        let cartItems = await response.json()
        setCartItems(cartItems)
        setLoading(false)
    }

    useEffect(() => {
        fetchCartItems(props.expectedVersion)
        setExpectedVersion(props.expectedVersion)
    }, [props.expectedVersion]);
    useEffect(() => {
        fetchCartItems(expectedVersion)
    }, [expectedVersion]);

    return <div>
        {loading? <Blocks
            height="80"
            width="80"
            color="#4fa94d"
            ariaLabel="blocks-loading"
            wrapperStyle={{}}
            wrapperClass="blocks-wrapper"
            visible={true}
        /> : <span/>}
        <div className="columns is-vcentered">
            <div className="column">
                {cartItems.data.map(item => {
                    return <div>
                        <div className="is-flex is-align-items-center">
                            <i className="fa-solid fa-mug-saucer p-2"></i>
                            <span className="is-3 mr-3">{item.description}</span>
                            <span>{item.price}</span>
                            <div className={"left-margin button is-info"} onClick={async () => {
                                let result = await removeItem(props.cartId, item.itemId)
                                setExpectedVersion(result.aggregateSequence)
                            }}>Remove Item
                            </div>
                        </div>
                        <div className={"top-margin"}>
                        <Inventories productId={item.productId}/>
                        </div>
                        <div>
                    </div>

                    </div>
                })}
            </div>
            <div className={"column"} onClick={() => fetchCartItems(expectedVersion)}>
                <i className="fa-solid fa-arrows-rotate big-reload"></i>
            </div>

        </div>
    </div>
}