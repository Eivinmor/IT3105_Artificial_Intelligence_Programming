import gym
import random

# WEST = 0
# SOUTH = 1
# EAST = 2
# NORTH = 3
direction = ['W', 'S', 'E', 'N']

max_episodes = 10000
epsilon = 0.1
epsilon_decay = 0.00001  # 0.00001
discount = 0.99
learning_rate = 0.1
learning_decay = 0.00001  # 0.00001


def run_algorithm(env, q_dict):
    observation = env.reset()
    done = False
    c = 1
    total_rewards = 0
    global epsilon, learning_rate
    next_action = get_epsilon_greedy_action(q_dict[observation], env.action_space.n)
    while not done:
        c += 1
        action = next_action
        prev_observation = observation
        observation, reward, done, info = env.step(action)
        next_action = get_epsilon_greedy_action(q_dict[observation], env.action_space.n)

        set_q_value_q(prev_observation, observation, action, reward, q_dict)
        # set_q_value_sarsa(prev_observation, observation, action, next_action, reward, q_dict)

        epsilon *= 1-epsilon_decay
        learning_rate *= 1-learning_decay
        total_rewards += reward
    return total_rewards


def initiate_q_dict(total_observations, total_actions):
    q_dict = {}
    for i in range(total_observations):
        q_dict[i] = [0]*total_actions
    return q_dict


def get_epsilon_greedy_action(q_values, total_actions):
    random_nr = random.random()
    if 1-epsilon > random_nr and max(q_values) != 0:
        best_action = q_values.index(max(q_values))
        return best_action
    random_action = random.randint(0, total_actions-1)
    return random_action


def set_q_value_q(state, next_state, action, reward, q_dict):
    q_dict[state][action] += \
        learning_rate*(reward + discount*(max(q_dict[next_state])) - q_dict[state][action])
    # Q(s_t, a_t) += a[r_t+1 + Y * (max(a) Q(s_t+1, a)) - Q(s_t, a_t)]


def set_q_value_sarsa(state, next_state, action, next_action, reward, q_dict):
    q_dict[state][action] += \
        learning_rate*(reward + discount*(q_dict[next_state][next_action]) - q_dict[state][action])
    # Q(s_t, a_t) += a[r_t+1 + Y * Q(s_t+1, a_t+1) - Q(s_t, a_t)]


def print_env(env):
    env.render()
    print()


def main():
    total_rewards = 0
    env = gym.make('Taxi-v1')
    q_dict = initiate_q_dict(env.observation_space.n, env.action_space.n)
    episode = 0
    while episode < max_episodes:
        total_rewards += run_algorithm(env, q_dict)
        episode += 1
        if episode % 100 == 0:
            print("{:>5}".format(episode), "\t", "{:<6}".format(total_rewards/100))
            total_rewards = 0
        elif episode == 1:
            print("{:>5}".format(episode), "\t",  "{:<6}".format(total_rewards))


main()
