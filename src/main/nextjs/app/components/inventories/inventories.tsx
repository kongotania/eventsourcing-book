import {useEffect, useState} from "react";
import {removeItem} from "@/app/components/removeItem/removeItem";

export const Inventories = (props: { productId: string }) => {

    const [inventory, setInventory] = useState<{ inventory:number }>({inventory: 0})
    const [expectedVersion, setExpectedVersion] = useState(-1)

    const fetchInventories = async () => {
        let response = await fetch(`/inventories/${props.productId}?expectedVersion=${expectedVersion??-1}`)
        let invntories = await response.json()
        setInventory(invntories.data)
    }

    useEffect(() => {
        let ref =setInterval(async ()=>{
            let version = await fetch(`/inventories/${props.productId}/version`)
            let sequence = await version.json()
            setExpectedVersion(sequence.sequenceNumber)
        }, 5000)
        return ()=>{clearInterval(ref)}
    }, []);

    useEffect(() => {
        fetchInventories()
    }, [expectedVersion]);

    return <div>
        <div className="columns is-vcentered">
            <div className="column">
                        <div className="tag is-warning is-flex is-align-items-center">
                           Available: {inventory.inventory}
                        </div>
            </div>
            <div className={"column"} onClick={() => fetchInventories()}>
                <i className="fa-solid fa-arrows-rotate"></i>
            </div>

        </div>
    </div>
}