import {v4} from "uuid";
import {CommandResult} from "@/app/components/types/CommandResult";

export const removeItem = async (cartId:string, itemId:string):Promise<CommandResult> => {
    let payload = {
        aggregateId: cartId,
        itemId: itemId
    }
    let response = await fetch(`/removeitem/${cartId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: `${JSON.stringify(payload)}`
    })
    return response.json()
}