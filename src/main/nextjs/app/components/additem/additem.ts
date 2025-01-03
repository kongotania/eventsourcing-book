import {v4} from "uuid";
import {CommandResult} from "@/app/components/types/CommandResult";


export const addItem = async (cartId:string, productId: string): Promise<CommandResult> => {
    let response = await fetch(`additem/${cartId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: `{
                "aggregateId": "${cartId}",
                "description": "premium coffee brand",
                "image": "string",
                "price": 9.99,
                "itemId": "${v4()}",
                "productId": "${productId}"
            }`
    })
    return await response.json()
}