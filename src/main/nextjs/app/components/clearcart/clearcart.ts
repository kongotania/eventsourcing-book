import {v4} from "uuid";
import {CommandResult} from "@/app/components/types/CommandResult";

export const clearcart = async (cartId:string): Promise<CommandResult> => {
    let response = await fetch(`clearcart/${cartId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    return response.json()
}