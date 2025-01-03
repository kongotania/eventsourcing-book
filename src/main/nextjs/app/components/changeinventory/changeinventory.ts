import {v4} from "uuid";
import {CommandResult} from "@/app/components/types/CommandResult";

export const changeInventory = async (productId: string, inventory:number): Promise<CommandResult> => {
    let response = await fetch(`/debug/external/changeinventory?productId=${productId}&inventory=${inventory}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    return response.json()
}