import {useEffect, useState} from "react";
import {removeItem} from "@/app/components/removeItem/removeItem";

export const Inventories = (props: { productId: string }) => {

    const [inventory, setInventory] = useState()

    useEffect(() => {
        const eventSource = new EventSource(`/inventories/sse/${props.productId}`, {
        });
        eventSource.onmessage = (event) => {
            let payload = JSON.parse(event.data)
            if (payload.data !== "heartbeat") {
                setInventory(payload.data.inventory)
            }
        };

        return () => {
            alert("close")
                eventSource.close();
            }
    }, []);


    return <div>
        <div className="columns is-vcentered">
            <div className="column">
                        <div className="tag is-warning is-flex is-align-items-center">
                           Available: {inventory}
                        </div>
            </div>
        </div>
    </div>
}